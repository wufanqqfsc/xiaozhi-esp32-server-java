package com.xiaozhi.ai.tts;

import com.xiaozhi.utils.EmojiUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceHelper implements ChatConverter {

    public record SentenceResult(String text, String mood) {}

    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[。！？!?]");

    private static final Pattern PAUSE_PATTERN = Pattern.compile("[，、；,;]");

    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[：:\"]");

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("[\n\r]");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+\\.\\d+");

    private static final int MIN_SENTENCE_LENGTH = 8;

    private static final int CONTEXT_BUFFER_MAX_LENGTH = 20;

    private static final String THINK_START_TAG = "<think>";
    private static final String THINK_END_TAG = "</think>";

    private final StringBuilder currentSentence = new StringBuilder();
    private final StringBuilder contextBuffer = new StringBuilder();

    private boolean insideThink = false;
    private final StringBuilder thinkBuffer = new StringBuilder();

    public SentenceHelper() {
    }

    public List<SentenceResult> take(String token) {
        List<SentenceResult> sentences = new ArrayList<>();
        if (token == null || token.isEmpty()) {
            return sentences;
        }

        StringBuilder contentBuffer = new StringBuilder();
        int i = 0;
        while (i < token.length()) {
            if (insideThink) {
                int endTagPos = token.indexOf(THINK_END_TAG, i);
                if (endTagPos >= 0) {
                    thinkBuffer.append(token.substring(i, endTagPos));
                    i = endTagPos + THINK_END_TAG.length();
                    insideThink = false;
                    thinkBuffer.setLength(0);
                } else {
                    thinkBuffer.append(token.substring(i));
                    i = token.length();
                }
            } else {
                int startTagPos = token.indexOf(THINK_START_TAG, i);
                if (startTagPos >= 0) {
                    if (startTagPos > i) {
                        contentBuffer.append(token.substring(i, startTagPos));
                    }
                    i = startTagPos + THINK_START_TAG.length();
                    insideThink = true;
                } else {
                    contentBuffer.append(token.substring(i));
                    i = token.length();
                }
            }
        }

        String cleanContent = contentBuffer.toString();
        if (cleanContent.isEmpty()) {
            return sentences;
        }

        for (int j = 0; j < cleanContent.length();) {
            int codePoint = cleanContent.codePointAt(j);
            String charStr = new String(Character.toChars(codePoint));

            contextBuffer.append(charStr);
            if (contextBuffer.length() > CONTEXT_BUFFER_MAX_LENGTH) {
                contextBuffer.delete(0, contextBuffer.length() - CONTEXT_BUFFER_MAX_LENGTH);
            }

            currentSentence.append(charStr);

            boolean isEndMark = SENTENCE_END_PATTERN.matcher(charStr).find();
            boolean isPauseMark = PAUSE_PATTERN.matcher(charStr).find();
            boolean isSpecialMark = SPECIAL_PATTERN.matcher(charStr).find();
            boolean isNewline = NEWLINE_PATTERN.matcher(charStr).find();
            boolean isEmoji = EmojiUtils.isEmoji(codePoint);

            boolean containsKaomoji = false;
            if (currentSentence.length() >= 3) {
                containsKaomoji = EmojiUtils.containsKaomoji(currentSentence.toString());
            }

            if (isEndMark && charStr.equals(".")) {
                String context = contextBuffer.toString();
                Matcher numberMatcher = NUMBER_PATTERN.matcher(context);
                if (numberMatcher.find() && numberMatcher.end() >= context.length() - 3) {
                    isEndMark = false;
                }
            }

            boolean shouldSendSentence = false;
            if (isEndMark || isNewline) {
                shouldSendSentence = true;
            } else if ((isPauseMark || isSpecialMark || isEmoji || containsKaomoji)
                    && currentSentence.length() >= MIN_SENTENCE_LENGTH) {
                shouldSendSentence = true;
            }

            if (shouldSendSentence && currentSentence.length() >= MIN_SENTENCE_LENGTH) {
                String rawSentence = currentSentence.toString().trim();
                List<String> moods = new ArrayList<>();
                String cleanSentence = EmojiUtils.processSentence(rawSentence, moods);
                if (containsSubstantialContent(cleanSentence)) {
                    String mood = moods.isEmpty() ? null : moods.get(0);
                    sentences.add(new SentenceResult(cleanSentence, mood));
                    currentSentence.setLength(0);
                }
            }

            j += Character.charCount(codePoint);
        }

        return sentences;
    }

    public SentenceResult take() {
        String rawSentence = currentSentence.toString().trim();
        if (rawSentence.isEmpty()) {
            return new SentenceResult("", null);
        }
        List<String> moods = new ArrayList<>();
        String cleanSentence = EmojiUtils.processSentence(rawSentence, moods);
        String mood = moods.isEmpty() ? null : moods.get(0);
        return new SentenceResult(cleanSentence, mood);
    }

    public void onToken(String token, FluxSink<SentenceResult> sink) {
        for (SentenceResult result : take(token)) {
            sink.next(result);
        }
    }

    public void onComplete(FluxSink<SentenceResult> sink) {
        SentenceResult result = take();
        if (StringUtils.hasText(result.text())) {
            sink.next(result);
        }
        sink.complete();
    }

    public Flux<SentenceResult> convert(Flux<String> stringFlux) {
        return Flux.create(sink ->
                stringFlux.subscribe(
                        token -> this.onToken(token, sink),
                        sink::error,
                        () -> this.onComplete(sink)));
    }

    private boolean containsSubstantialContent(String text) {
        if (text == null || text.trim().length() < MIN_SENTENCE_LENGTH) {
            return false;
        }
        String stripped = text.replaceAll("[\\p{P}\\s]", "");
        return stripped.length() >= 2;
    }
}

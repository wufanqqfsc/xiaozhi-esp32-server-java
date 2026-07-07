<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ThunderboltOutlined, RightOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  content: string
  done?: boolean
  expanded?: boolean
}>()

defineEmits<{
  (e: 'toggle'): void
}>()

const { t } = useI18n()

const displayContent = computed(() => {
  return props.content
})
</script>

<template>
  <div class="thinking-block">
    <div class="thinking-header" @click="$emit('toggle')">
      <RightOutlined class="thinking-arrow" :class="{ expanded }" />
      <ThunderboltOutlined class="thinking-icon" :class="{ active: !done }" />
      <span v-if="done" class="thinking-status done">{{ t('chat.thinkingDone') }}</span>
      <span v-else class="thinking-status">
        {{ t('chat.thinkingInProgress') }}
        <span class="thinking-dots">
          <span></span><span></span><span></span>
        </span>
      </span>
    </div>
    <div v-show="expanded" class="thinking-content">
      <div class="thinking-content-inner" :class="{ typing: !done }">
        {{ displayContent }}
        <span v-if="!done" class="thinking-cursor"></span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.thinking-block {
  margin-bottom: 8px;
  border-radius: 8px;
  background: #f5f5f5;
  overflow: hidden;
  border: 1px solid transparent;
  transition: border-color 0.3s;
}

.thinking-block:hover {
  border-color: #d9d9d9;
}

.thinking-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
  color: #8c8c8c;
  user-select: none;
  transition: color 0.2s, background 0.2s;
}

.thinking-header:hover {
  color: #595959;
  background: rgba(0, 0, 0, 0.02);
}

.thinking-arrow {
  font-size: 10px;
  transition: transform 0.2s ease;
}

.thinking-arrow.expanded {
  transform: rotate(90deg);
}

.thinking-icon {
  font-size: 13px;
  transition: color 0.3s, transform 0.3s;
}

.thinking-icon.active {
  color: #722ed1;
  animation: icon-pulse 1.5s ease-in-out infinite;
}

@keyframes icon-pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.thinking-status {
  display: flex;
  align-items: center;
  gap: 4px;
}

.thinking-status.done {
  color: #52c41a;
}

.thinking-dots {
  display: flex;
  gap: 2px;
}

.thinking-dots span {
  width: 3px;
  height: 3px;
  background: #8c8c8c;
  border-radius: 50%;
  animation: dot-bounce 1.4s infinite ease-in-out both;
}

.thinking-dots span:nth-child(1) { animation-delay: -0.32s; }
.thinking-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes dot-bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.thinking-content {
  padding: 8px 12px 10px 30px;
  font-size: 13px;
  line-height: 1.6;
  color: #8c8c8c;
  white-space: pre-wrap;
  word-break: break-word;
  border-top: 1px solid #e8e8e8;
  margin: 0 12px;
  animation: content-fade-in 0.3s ease;
}

@keyframes content-fade-in {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.thinking-content-inner {
  position: relative;
}

.thinking-cursor {
  display: inline-block;
  width: 2px;
  height: 14px;
  background: #722ed1;
  animation: cursor-blink 1s infinite;
  vertical-align: middle;
  margin-left: 2px;
}

@keyframes cursor-blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>

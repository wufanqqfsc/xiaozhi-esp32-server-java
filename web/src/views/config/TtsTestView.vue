<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message as antMessage } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { SoundOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { testTts } from '@/services/tts'
import { queryConfigs } from '@/services/config'
import AudioPlayer from '@/components/AudioPlayer.vue'
import type { Config } from '@/types/config'

const { t } = useI18n()

const text = ref('你好，我是贾维斯，很高兴为您服务。')
const speed = ref(1.0)
const pitch = ref(1.0)
const voiceName = ref('zh-CN-XiaoyiNeural')
const configId = ref<number | undefined>(undefined)
const configList = ref<Config[]>([])
const isLoading = ref(false)
const audioUrl = ref('')
const audioKey = ref(0)

async function loadConfigList() {
  try {
    const res = await queryConfigs({
      configType: 'tts',
      pageNum: 1,
      pageSize: 100,
    })
    const list = res?.data?.list ?? []
    configList.value = list
    const defaultConfig = list.find((c: Config) => c.isDefault && c.configId != null)
    if (defaultConfig) {
      configId.value = defaultConfig.configId as number
    } else {
      const firstConfig = list.find((c: Config) => c.configId != null)
      if (firstConfig) {
        configId.value = firstConfig.configId as number
      }
    }
  } catch (err) {
    console.error('加载TTS配置列表失败', err)
  }
}

async function handleTest() {
  if (!text.value.trim()) {
    antMessage.warning('请输入要合成的文本')
    return
  }

  isLoading.value = true
  audioUrl.value = ''

  try {
    const blob = await testTts({
      configId: configId.value,
      voiceName: voiceName.value,
      speed: speed.value,
      pitch: pitch.value,
      text: text.value,
    })

    if (audioUrl.value) {
      URL.revokeObjectURL(audioUrl.value)
    }
    audioUrl.value = URL.createObjectURL(blob)
    audioKey.value++
    antMessage.success('语音合成成功')
  } catch (err: unknown) {
    console.error('TTS合成失败', err)
    if (err instanceof Error) {
      antMessage.error(err.message || '语音合成失败')
    } else {
      antMessage.error('语音合成失败')
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadConfigList()
})
</script>

<template>
  <div class="tts-test-container">
    <a-card title="TTS 语音合成测试" :bordered="false">
      <a-form layout="vertical">
        <a-form-item label="TTS 配置">
          <a-select
            v-model:value="configId"
            placeholder="请选择TTS配置"
            style="width: 100%"
            :options="configList.map(c => ({
              label: `${c.provider} - ${c.configName}${c.isDefault ? ' (默认)' : ''}`,
              value: c.configId,
            }))"
          />
        </a-form-item>

        <a-form-item label="音色名称">
          <a-input
            v-model:value="voiceName"
            placeholder="请输入音色名称，如 zh-CN-XiaoyiNeural"
          />
        </a-form-item>

        <a-form-item label="语速 (0.5 - 2.0)">
          <a-slider
            v-model:value="speed"
            :min="0.5"
            :max="2.0"
            :step="0.1"
            :marks="{ 0.5: '0.5x', 1.0: '1.0x', 1.5: '1.5x', 2.0: '2.0x' }"
          />
        </a-form-item>

        <a-form-item label="音调 (0.5 - 2.0)">
          <a-slider
            v-model:value="pitch"
            :min="0.5"
            :max="2.0"
            :step="0.1"
            :marks="{ 0.5: '0.5x', 1.0: '1.0x', 1.5: '1.5x', 2.0: '2.0x' }"
          />
        </a-form-item>

        <a-form-item label="合成文本">
          <a-textarea
            v-model:value="text"
            :rows="4"
            placeholder="请输入要合成的文本内容"
            show-count
            :maxlength="500"
          />
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            :loading="isLoading"
            @click="handleTest"
          >
            <template #icon>
              <LoadingOutlined v-if="isLoading" />
              <SoundOutlined v-else />
            </template>
            合成并播放
          </a-button>
        </a-form-item>
      </a-form>

      <div v-if="audioUrl" class="audio-player-wrapper">
        <a-divider>播放结果</a-divider>
        <AudioPlayer :key="audioKey" :audio-url="audioUrl" :auto-play="true" />
      </div>
    </a-card>
  </div>
</template>

<style scoped lang="scss">
.tts-test-container {
  padding: 16px;

  :deep(.ant-card-head) {
    border-bottom: 1px solid var(--ant-color-border-secondary);
  }

  .audio-player-wrapper {
    margin-top: 8px;
  }
}
</style>

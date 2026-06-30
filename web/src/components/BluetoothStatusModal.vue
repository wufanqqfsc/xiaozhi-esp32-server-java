<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Modal, Tag, Descriptions, Spin, Button, Space, Alert } from 'ant-design-vue'
import { getBluetoothStatus } from '@/services/device'
import type { BluetoothStatus } from '@/types/device'
import { SyncOutlined, CheckCircleOutlined, WifiOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

interface Props {
  visible: boolean
  deviceId: string
  deviceName: string
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const { t } = useI18n()

const loading = ref(false)
const bleStatus = ref<BluetoothStatus | null>(null)
const pollingTimer = ref<ReturnType<typeof setInterval> | null>(null)

// 监听弹窗打开
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadBleStatus()
    // 开始轮询，每3秒刷新状态
    startPolling()
  } else {
    stopPolling()
    bleStatus.value = null
  }
})

// 加载蓝牙状态
async function loadBleStatus() {
  loading.value = true
  try {
    const res = await getBluetoothStatus(props.deviceId)
    if (res.code === 200 && res.data) {
      bleStatus.value = res.data as BluetoothStatus
    } else {
      console.error('获取蓝牙状态失败:', res.message)
    }
  } catch (error) {
    console.error('获取蓝牙状态失败:', error)
  } finally {
    loading.value = false
  }
}

// 开始轮询
function startPolling() {
  stopPolling()
  pollingTimer.value = setInterval(() => {
    loadBleStatus()
  }, 3000)
}

// 停止轮询
function stopPolling() {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

// 手动刷新
function handleRefresh() {
  loadBleStatus()
}

// 关闭弹窗
function handleClose() {
  emit('update:visible', false)
  emit('close')
}

// 状态颜色
const statusColor = computed(() => {
  if (!bleStatus.value) return 'default'
  switch (bleStatus.value.bleStatusText) {
    case 'connected': return 'green'
    case 'advertising': return 'blue'
    case 'disabled': return 'orange'
    case 'paused': return 'purple'
    default: return 'red'
  }
})

// 状态标签文本
const statusLabel = computed(() => {
  if (!bleStatus.value) return ''
  switch (bleStatus.value.bleStatusText) {
    case 'connected': return t('bluetooth.connected')
    case 'advertising': return t('bluetooth.advertising')
    case 'disabled': return t('bluetooth.disabled')
    case 'paused': return t('bluetooth.paused')
    case 'not_supported': return t('bluetooth.notSupported')
    case 'offline': return t('bluetooth.offline')
    case 'no_ip': return t('bluetooth.noIp')
    default: return bleStatus.value.message || t('bluetooth.unknown')
  }
})

// 状态图标
const statusIcon = computed(() => {
  if (!bleStatus.value) return null
  switch (bleStatus.value.bleStatusText) {
    case 'connected': return CheckCircleOutlined
    case 'advertising': return WifiOutlined
    case 'disabled':
    case 'offline':
    case 'no_ip': return CloseCircleOutlined
    case 'paused': return WifiOutlined
    default: return CloseCircleOutlined
  }
})
</script>

<template>
  <Modal
    :open="visible"
    :title="t('bluetooth.bluetoothConnection')"
    :footer="null"
    :width="480"
    @cancel="handleClose"
  >
    <Spin :spinning="loading">
      <!-- 设备信息 -->
      <div class="device-info">
        <span class="device-label">{{ t('bluetooth.device') }}:</span>
        <span class="device-name">{{ deviceName || deviceId }}</span>
      </div>

      <!-- 状态显示 -->
      <div class="status-section">
        <div class="status-header">
          <span class="status-label">{{ t('bluetooth.status') }}:</span>
          <Tag :color="statusColor" class="status-tag">
            <component :is="statusIcon" v-if="statusIcon" />
            {{ statusLabel }}
          </Tag>
        </div>

        <!-- 状态详情 -->
        <div v-if="bleStatus" class="status-details">
          <Descriptions :column="1" size="small" bordered>
            <Descriptions.Item :label="t('bluetooth.online')">
              {{ bleStatus.online ? t('common.yes') : t('common.no') }}
            </Descriptions.Item>
            <Descriptions.Item :label="t('bluetooth.bleEnabled')">
              {{ bleStatus.bleEnabled ? t('common.yes') : t('common.no') }}
            </Descriptions.Item>
            <Descriptions.Item :label="t('bluetooth.statusCode')">
              {{ bleStatus.bleStatus }} ({{ bleStatus.bleStatusText }})
            </Descriptions.Item>
          </Descriptions>
        </div>
      </div>

      <!-- 提示信息 -->
      <Alert
        v-if="bleStatus"
        :message="t('bluetooth.tips')"
        :description="bleStatus.message"
        :type="bleStatus.bleStatusText === 'advertising' ? 'info' : bleStatus.bleStatusText === 'connected' ? 'success' : 'warning'"
        show-icon
        class="tips-alert"
      />

      <!-- 操作按钮 -->
      <div class="actions">
        <Space>
          <Button :icon="h(SyncOutlined)" @click="handleRefresh">
            {{ t('common.refresh') }}
          </Button>
          <Button type="primary" @click="handleClose">
            {{ t('common.close') }}
          </Button>
        </Space>
      </div>
    </Spin>
  </Modal>
</template>

<script lang="ts">
// 使用 h 函数渲染图标
import { h } from 'vue'
export default {
  setup() {
    return { h }
  }
}
</script>

<style scoped lang="scss">
.device-info {
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 16px;

  .device-label {
    color: #666;
    margin-right: 8px;
  }

  .device-name {
    font-weight: 500;
    color: #333;
  }
}

.status-section {
  margin-bottom: 16px;

  .status-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .status-label {
      font-weight: 500;
      margin-right: 8px;
    }

    .status-tag {
      font-size: 14px;
      padding: 4px 12px;

      :deep(.anticon) {
        margin-right: 4px;
      }
    }
  }

  .status-details {
    margin-top: 8px;
  }
}

.tips-alert {
  margin-bottom: 16px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}
</style>

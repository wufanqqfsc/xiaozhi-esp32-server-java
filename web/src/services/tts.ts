import request, { http } from './request'
import api from './api'

export interface TtsTestParams {
  configId?: number
  voiceName?: string
  speed?: number
  pitch?: number
  text: string
}

export function testTts(params: TtsTestParams): Promise<Blob> {
  const query = new URLSearchParams()
  if (params.configId) query.set('configId', String(params.configId))
  if (params.voiceName) query.set('voiceName', params.voiceName)
  if (params.speed) query.set('speed', String(params.speed))
  if (params.pitch) query.set('pitch', String(params.pitch))
  query.set('text', params.text)

  return request.post(`${api.tts.test}?${query.toString()}`, null, {
    responseType: 'blob',
    timeout: 60000,
  }).then(res => res.data as Blob)
}

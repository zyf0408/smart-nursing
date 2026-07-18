<template>
  <el-upload
    :action="uploadUrl"
    :headers="uploadHeaders"
    :data="uploadData"
    :before-upload="beforeUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    :on-remove="handleRemove"
    :on-preview="handlePreview"
    :file-list="fileList"
    :list-type="listType"
    :limit="limit"
    :accept="accept"
    :show-file-list="showFileList"
    :drag="drag"
    name="file"
  >
    <template v-if="listType === 'picture-card'">
      <el-icon><Plus /></el-icon>
    </template>
    <template v-else-if="drag">
      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
    </template>
    <template v-else>
      <el-button type="primary" :icon="Upload">{{ buttonText }}</el-button>
    </template>
    <template #tip>
      <div v-if="tip" class="el-upload__tip">{{ tip }}</div>
    </template>
  </el-upload>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload, UploadFilled } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  // 上传地址
  action: {
    type: String,
    default: '/api/common/upload'
  },
  // 限制数量
  limit: {
    type: Number,
    default: 1
  },
  // 文件类型
  accept: {
    type: String,
    default: ''
  },
  // 列表类型 text/picture/picture-card
  listType: {
    type: String,
    default: 'text'
  },
  // 是否拖拽上传
  drag: {
    type: Boolean,
    default: false
  },
  // 按钮文字
  buttonText: {
    type: String,
    default: '点击上传'
  },
  // 提示文字
  tip: {
    type: String,
    default: ''
  },
  // 最大文件大小（MB）
  maxSize: {
    type: Number,
    default: 10
  },
  // 是否显示文件列表
  showFileList: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'success', 'remove'])

const baseURL = import.meta.env.VITE_API_BASE_URL
const uploadUrl = computed(() => `${baseURL}${props.action.replace('/api', '')}`)
const uploadHeaders = computed(() => ({
  token: localStorage.getItem('token') || ''
}))
const uploadData = ref({})

const fileList = ref([])

// 监听 modelValue 变化，同步 fileList
watch(
  () => props.modelValue,
  (val) => {
    if (typeof val === 'string' && val) {
      if (fileList.value.length === 0 || fileList.value[0].url !== val) {
        fileList.value = [{ name: val.split('/').pop(), url: val }]
      }
    } else if (Array.isArray(val)) {
      fileList.value = val.map((url) => ({ name: url.split('/').pop(), url }))
    } else {
      fileList.value = []
    }
  },
  { immediate: true }
)

const beforeUpload = (file) => {
  const isLtMax = file.size / 1024 / 1024 < props.maxSize
  if (!isLtMax) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

const handleSuccess = (response, file) => {
  if (response.code === 200) {
    const url = response.data?.url || response.data
    file.url = url
    if (props.limit === 1) {
      emit('update:modelValue', url)
    } else {
      const current = Array.isArray(props.modelValue) ? [...props.modelValue] : []
      current.push(url)
      emit('update:modelValue', current)
    }
    emit('success', response, file)
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleError = () => {
  ElMessage.error('上传失败，请稍后重试')
}

const handleRemove = (file) => {
  if (props.limit === 1) {
    emit('update:modelValue', '')
  } else {
    const current = Array.isArray(props.modelValue) ? [...props.modelValue] : []
    const index = current.indexOf(file.url)
    if (index > -1) {
      current.splice(index, 1)
    }
    emit('update:modelValue', current)
  }
  emit('remove', file)
}

const handlePreview = (file) => {
  if (file.url) {
    window.open(file.url, '_blank')
  }
}
</script>

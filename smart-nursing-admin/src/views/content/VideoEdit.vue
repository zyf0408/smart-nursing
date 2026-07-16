<template>
  <div class="app-container">
    <div class="card-box">
      <div class="edit-header">
        <el-page-header @back="goBack">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑视频' : '新增视频' }}</span>
          </template>
        </el-page-header>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="edit-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入视频标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="类别" prop="categoryId">
          <el-tree-select
            v-model="form.categoryId"
            :data="categoryOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            default-expand-all
            placeholder="请选择类别"
            clearable
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <FileUpload v-model="form.coverImage" list-type="picture-card" :limit="1" accept="image/*" tip="建议尺寸 800x450" />
        </el-form-item>
        <el-form-item label="视频文件" prop="videoUrl">
          <FileUpload v-model="form.videoUrl" :limit="1" accept="video/*" :maxSize="500" buttonText="上传视频" tip="支持 mp4/mov，最大 500MB" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import FileUpload from '@/components/FileUpload.vue'
import { videoGetById, videoAdd, videoUpdate } from '@/api/video'
import { categoryTree } from '@/api/category'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const submitLoading = ref(false)
const categoryOptions = ref([])

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  title: '',
  categoryId: '',
  coverImage: '',
  videoUrl: '',
  description: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  videoUrl: [{ required: true, message: '请上传视频文件', trigger: 'change' }]
}

const loadCategoryOptions = () => {
  categoryTree()
    .then((res) => {
      categoryOptions.value = res || []
    })
    .catch((err) => {
      console.error('获取类别失败', err)
    })
}

const loadVideo = () => {
  if (!route.query.id) return
  videoGetById(route.query.id)
    .then((res) => {
      Object.assign(form, res)
    })
    .catch((err) => {
      console.error('获取视频失败', err)
    })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const api = form.id ? videoUpdate(form) : videoAdd(form)
    api
      .then(() => {
        ElMessage.success(form.id ? '修改成功' : '新增成功')
        router.push('/content/video')
      })
      .catch((err) => {
        console.error('保存失败', err)
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

const goBack = () => {
  router.push('/content/video')
}

onMounted(() => {
  loadCategoryOptions()
  loadVideo()
})
</script>

<style scoped lang="scss">
.edit-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;

  .page-title {
    font-size: 16px;
    font-weight: bold;
  }
}

.edit-form {
  max-width: 700px;
}
</style>

<template>
  <div class="app-container">
    <div class="card-box">
      <div class="edit-header">
        <el-page-header @back="goBack">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑PPT' : '新增PPT' }}</span>
          </template>
        </el-page-header>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="edit-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入PPT标题" maxlength="100" show-word-limit />
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
        <el-form-item label="PPT文件" prop="fileUrl">
          <FileUpload v-model="form.fileUrl" :limit="1" accept=".ppt,.pptx" :maxSize="100" buttonText="上传PPT" tip="支持 ppt/pptx，最大 100MB" />
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
import { pptGetById, pptAdd, pptUpdate } from '@/api/ppt'
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
  fileUrl: '',
  description: '',
  status: 0
})

const rules = {
  title: [{ required: true, message: '请输入PPT标题', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请上传PPT文件', trigger: 'change' }]
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

const loadPpt = () => {
  if (!route.query.id) return
  pptGetById(route.query.id)
    .then((res) => {
      Object.assign(form, res)
    })
    .catch((err) => {
      console.error('获取PPT失败', err)
    })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const api = form.id ? pptUpdate(form) : pptAdd(form)
    api
      .then(() => {
        ElMessage.success(form.id ? '修改成功' : '新增成功')
        router.push('/content/ppt')
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
  router.push('/content/ppt')
}

onMounted(() => {
  loadCategoryOptions()
  loadPpt()
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

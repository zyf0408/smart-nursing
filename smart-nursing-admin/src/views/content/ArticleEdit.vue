<template>
  <div class="app-container">
    <div class="card-box">
      <div class="edit-header">
        <el-page-header @back="goBack">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑文章' : '新增文章' }}</span>
          </template>
        </el-page-header>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="edit-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
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
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入摘要" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <FileUpload v-model="form.coverImage" list-type="picture-card" :limit="1" accept="image/*" tip="建议尺寸 800x450，支持 jpg/png" />
        </el-form-item>
        <el-form-item label="标签" prop="tagIds">
          <el-select v-model="form.tagIds" multiple filterable placeholder="请选择标签" style="width: 100%">
            <el-option
              v-for="tag in tagOptions"
              :key="tag.id"
              :label="tag.tagName"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <QuillEditor
            ref="quillRef"
            v-model:content="form.content"
            contentType="html"
            :options="quillOptions"
            style="height: 400px; margin-bottom: 50px"
          />
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
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import FileUpload from '@/components/FileUpload.vue'
import { articleGetById, articleAdd, articleUpdate } from '@/api/article'
import { categoryTree } from '@/api/category'
import { tagAll } from '@/api/tag'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const quillRef = ref()
const submitLoading = ref(false)
const categoryOptions = ref([])
const tagOptions = ref([])

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  id: null,
  title: '',
  categoryId: '',
  summary: '',
  coverImage: '',
  tagIds: [],
  status: 0,
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择类别', trigger: 'change' }]
}

const quillOptions = {
  placeholder: '请输入文章正文...',
  modules: {
    toolbar: [
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ color: [] }, { background: [] }],
      [{ align: [] }, { list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image'],
      ['blockquote', 'code-block'],
      ['clean']
    ]
  }
}

const loadOptions = () => {
  categoryTree()
    .then((res) => {
      categoryOptions.value = res || []
    })
    .catch((err) => {
      console.error('获取类别失败', err)
    })
  tagAll()
    .then((res) => {
      tagOptions.value = res || []
    })
    .catch((err) => {
      console.error('获取标签失败', err)
    })
}

const loadArticle = () => {
  if (!route.query.id) return
  articleGetById(route.query.id)
    .then((res) => {
      Object.assign(form, res)
      form.tagIds = res.tagIds || []
    })
    .catch((err) => {
      console.error('获取文章失败', err)
    })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const api = form.id ? articleUpdate(form) : articleAdd(form)
    api
      .then(() => {
        ElMessage.success(form.id ? '修改成功' : '新增成功')
        router.push('/content/article')
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
  router.push('/content/article')
}

onMounted(() => {
  loadOptions()
  loadArticle()
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
  max-width: 900px;
}
</style>

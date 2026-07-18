<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 工具栏 -->
      <div class="table-toolbar">
        <span class="toolbar-title">类别管理</span>
        <el-button type="success" :icon="Plus" @click="handleAdd(null)">新增类别</el-button>
      </div>

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="categoryId"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="categoryName" label="类别名称" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Plus" @click="handleAdd(row)">新增</el-button>
            <el-button type="warning" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="上级类别" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryOptions"
            :props="{ label: 'categoryName', value: 'categoryId', children: 'children' }"
            check-strictly
            default-expand-all
            placeholder="请选择上级类别（顶级留空）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="类别名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入类别名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { categoryTree, categoryAdd, categoryUpdate, categoryDelete } from '@/api/category'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const categoryOptions = ref([])

const loadData = () => {
  loading.value = true
  categoryTree()
    .then((res) => {
      tableData.value = res || []
      categoryOptions.value = [{ categoryId: 0, categoryName: '顶级类别', children: res || [] }]
    })
    .catch((err) => {
      console.error('获取类别树失败', err)
    })
    .finally(() => {
      loading.value = false
    })
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增类别')
const formRef = ref()
const form = reactive({
  categoryId: null,
  parentId: 0,
  categoryName: '',
  sortOrder: 0,
  description: ''
})

const rules = {
  categoryName: [{ required: true, message: '请输入类别名称', trigger: 'blur' }]
}

const handleAdd = (row) => {
  dialogTitle.value = '新增类别'
  Object.assign(form, { categoryId: null, parentId: row ? row.categoryId : 0, categoryName: '', sortOrder: 0, description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑类别'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const submitData = { ...form }
    delete submitData.children
    const api = form.categoryId ? categoryUpdate(submitData) : categoryAdd(submitData)
    api
      .then(() => {
        ElMessage.success(form.categoryId ? '修改成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      })
      .catch((err) => {
        console.error('保存失败', err)
      })
      .finally(() => {
        submitLoading.value = false
      })
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除类别「${row.categoryName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      categoryDelete(row.categoryId)
        .then(() => {
          ElMessage.success('删除成功')
          loadData()
        })
        .catch((err) => {
          console.error('删除失败', err)
        })
    })
    .catch(() => {})
}

const resetForm = () => {
  formRef.value && formRef.value.resetFields()
}

loadData()
</script>

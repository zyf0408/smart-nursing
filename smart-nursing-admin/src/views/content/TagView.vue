<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.tagName"
          placeholder="标签名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增标签</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="tagName" label="标签名称" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称" />
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { tagList, tagAdd, tagUpdate, tagDelete } from '@/api/tag'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tagName: ''
})

const loadData = () => {
  loading.value = true
  tagList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取标签列表失败', err)
    })
    .finally(() => {
      loading.value = false
    })
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.tagName = ''
  queryParams.pageNum = 1
  loadData()
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增标签')
const formRef = ref()
const form = reactive({
  tagId: null,
  tagName: ''
})

const rules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

const handleAdd = () => {
  dialogTitle.value = '新增标签'
  Object.assign(form, { tagId: null, tagName: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑标签'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const api = form.tagId ? tagUpdate(form) : tagAdd(form)
    api
      .then(() => {
        ElMessage.success(form.tagId ? '修改成功' : '新增成功')
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
  ElMessageBox.confirm(`确定删除标签「${row.tagName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      tagDelete(row.tagId)
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

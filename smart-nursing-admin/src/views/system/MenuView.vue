<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 工具栏 -->
      <div class="table-toolbar">
        <span class="toolbar-title">菜单管理</span>
        <el-button type="success" :icon="Plus" @click="handleAdd(null)">新增菜单</el-button>
      </div>

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="menuId"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="title" label="菜单名称" min-width="200" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="160" />
        <el-table-column prop="component" label="组件路径" min-width="180" />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.children && row.children.length > 0 ? 'primary' : 'info'">
              {{ row.children && row.children.length > 0 ? '目录' : '菜单' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="可见" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === 1 ? 'success' : 'danger'">
              {{ row.visible === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            :props="{ label: 'title', value: 'menuId', children: 'children' }"
            check-strictly
            default-expand-all
            placeholder="请选择上级菜单（顶级留空）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称（如 User）" />
        </el-form-item>
        <el-form-item label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址（如 /system/user）" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="form.type === 2">
          <el-input v-model="form.component" placeholder="请输入组件路径（如 system/UserView）" />
        </el-form-item>
        <el-form-item label="路由名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入路由名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="是否可见" prop="visible">
          <el-switch v-model="form.visible" :active-value="1" :inactive-value="0" active-text="显示" inactive-text="隐藏" />
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
import { menuTree, menuAdd, menuUpdate, menuDelete } from '@/api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const menuOptions = ref([])

const loadData = () => {
  loading.value = true
  menuTree()
    .then((res) => {
      tableData.value = res || []
      menuOptions.value = [{ menuId: 0, title: '顶级菜单', children: res || [] }]
    })
    .catch((err) => {
      console.error('获取菜单树失败', err)
    })
    .finally(() => {
      loading.value = false
    })
}

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref()
const form = reactive({
  menuId: null,
  parentId: 0,
  type: 2,
  title: '',
  icon: '',
  path: '',
  component: '',
  name: '',
  sortOrder: 0,
  visible: 1
})

const rules = {
  title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const handleAdd = (row) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, {
    menuId: null,
    parentId: row ? row.menuId : 0,
    type: 2,
    title: '',
    icon: '',
    path: '',
    component: '',
    name: '',
    sortOrder: 0,
    visible: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, { ...row, type: (row.children && row.children.length > 0) ? 1 : 2 })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const submitData = { ...form }
    delete submitData.type
    delete submitData.children
    const api = form.menuId ? menuUpdate(submitData) : menuAdd(submitData)
    api
      .then(() => {
        ElMessage.success(form.menuId ? '修改成功' : '新增成功')
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
  ElMessageBox.confirm(`确定删除菜单「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      menuDelete(row.menuId)
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

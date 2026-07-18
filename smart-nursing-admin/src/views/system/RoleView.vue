<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.roleName"
          placeholder="角色名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="roleCode" label="角色编码" min-width="140" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :icon="Setting" @click="handleAssignMenus(row)">分配菜单</el-button>
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="dialogTitle === '编辑角色'" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="450px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        :props="{ label: 'title', children: 'children' }"
        node-key="menuId"
        show-checkbox
        default-expand-all
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="handleMenuAssignSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Setting } from '@element-plus/icons-vue'
import { roleList, roleAdd, roleUpdate, roleDelete, assignMenus, getRoleMenus } from '@/api/role'
import { menuTree } from '@/api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const assignLoading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: ''
})

const loadData = () => {
  loading.value = true
  roleList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取角色列表失败', err)
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
  queryParams.roleName = ''
  queryParams.pageNum = 1
  loadData()
}

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref()
const form = reactive({
  roleId: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, { roleId: null, roleName: '', roleCode: '', description: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitLoading.value = true
    const api = form.roleId ? roleUpdate(form) : roleAdd(form)
    api
      .then(() => {
        ElMessage.success(form.roleId ? '修改成功' : '新增成功')
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
  ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      roleDelete(row.roleId)
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

// 分配菜单
const menuDialogVisible = ref(false)
const menuTreeRef = ref()
const menuTreeData = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)

const handleAssignMenus = (row) => {
  currentRoleId.value = row.roleId
  checkedMenuIds.value = []
  menuTree()
    .then((menuRes) => {
      menuTreeData.value = menuRes || []
      return getRoleMenus(row.roleId)
    })
    .then((roleMenusRes) => {
      checkedMenuIds.value = roleMenusRes || []
      menuDialogVisible.value = true
    })
    .catch((err) => {
      console.error('获取菜单失败', err)
    })
}

const handleMenuAssignSubmit = () => {
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  assignLoading.value = true
  assignMenus({ roleId: currentRoleId.value, menuIds: [...checkedKeys, ...halfCheckedKeys] })
    .then(() => {
      ElMessage.success('分配成功')
      menuDialogVisible.value = false
    })
    .catch((err) => {
      console.error('分配菜单失败', err)
    })
    .finally(() => {
      assignLoading.value = false
    })
}

loadData()
</script>

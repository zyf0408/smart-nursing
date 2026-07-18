<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.title"
          placeholder="文章标题"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.categoryId" placeholder="类别" clearable style="width: 180px">
          <el-option
            v-for="item in categoryOptions"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId"
          />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增文章</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="类别" width="120" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { articleList, articleDelete } from '@/api/article'
import { categoryTree } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryOptions = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  categoryId: ''
})

// 扁平化类别树
const flattenCategory = (tree, result = []) => {
  tree.forEach((item) => {
    result.push({ categoryId: item.categoryId, categoryName: item.categoryName })
    if (item.children && item.children.length > 0) {
      flattenCategory(item.children, result)
    }
  })
  return result
}

const loadCategoryOptions = () => {
  categoryTree()
    .then((res) => {
      categoryOptions.value = flattenCategory(res || [])
    })
    .catch((err) => {
      console.error('获取类别失败', err)
    })
}

const loadData = () => {
  loading.value = true
  articleList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取文章列表失败', err)
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
  queryParams.title = ''
  queryParams.categoryId = ''
  queryParams.pageNum = 1
  loadData()
}

const handleAdd = () => {
  router.push('/content/article/edit')
}

const handleEdit = (row) => {
  router.push(`/content/article/edit?id=${row.articleId}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除文章「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      articleDelete(row.articleId)
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

onMounted(() => {
  loadCategoryOptions()
  loadData()
})
</script>

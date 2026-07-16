<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.username"
          placeholder="用户名"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.contentType" placeholder="内容类型" clearable style="width: 140px">
          <el-option label="文章" value="1" />
          <el-option label="视频" value="2" />
          <el-option label="PPT" value="3" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="contentTitle" label="学习内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contentType" label="内容类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ getContentTypeText(row.contentType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="学习时长(分)" width="120" align="center" />
        <el-table-column prop="progress" label="进度" width="120" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :stroke-width="8" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="学习时间" min-width="160" />
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
import { ref, reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { learningList } from '@/api/learning'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  contentType: ''
})

const getContentTypeText = (type) => {
  const map = { 1: '文章', 2: '视频', 3: 'PPT' }
  return map[type] || '未知'
}

const loadData = () => {
  loading.value = true
  learningList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取学习记录失败', err)
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
  queryParams.username = ''
  queryParams.contentType = ''
  queryParams.pageNum = 1
  loadData()
}

loadData()
</script>

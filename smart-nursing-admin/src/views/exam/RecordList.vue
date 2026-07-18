<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.username"
          placeholder="考生姓名"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="queryParams.examName"
          placeholder="考试名称"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="username" label="考生姓名" min-width="120" />
        <el-table-column prop="examName" label="考试名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="score" label="得分" width="80" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.score >= row.passScore ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="passScore" label="及格分" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score >= row.passScore ? 'success' : 'danger'">
              {{ row.score >= row.passScore ? '通过' : '不通过' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="用时(分)" width="90" align="center" />
        <el-table-column prop="submitTime" label="提交时间" min-width="160" />
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleDetail(row)">详情</el-button>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { recordList } from '@/api/examRecord'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  examName: ''
})

const loadData = () => {
  loading.value = true
  recordList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取成绩列表失败', err)
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
  queryParams.examName = ''
  queryParams.pageNum = 1
  loadData()
}

const handleDetail = (row) => {
  router.push(`/examRecord/detail?recordId=${row.recordId}`)
}

loadData()
</script>

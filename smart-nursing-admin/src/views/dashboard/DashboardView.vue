<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" v-for="(card, index) in statCards" :key="index">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-body">
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon :size="28" color="#fff">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ card.title }}</div>
              <div class="stat-value">{{ card.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>类别内容分布</span>
          </template>
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>学习趋势</span>
          </template>
          <div ref="lineChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dashboardData } from '@/api/dataCount'

const pieChartRef = ref(null)
const lineChartRef = ref(null)
let pieChart = null
let lineChart = null

const statCards = reactive([
  { title: '用户数', value: 0, icon: 'User', color: '#409eff' },
  { title: '文章数', value: 0, icon: 'Document', color: '#67c23a' },
  { title: '视频数', value: 0, icon: 'VideoPlay', color: '#e6a23c' },
  { title: '考试数', value: 0, icon: 'EditPen', color: '#f56c6c' }
])

const initPieChart = (data) => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        name: '内容分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: data || [
          { value: 0, name: '暂无数据' }
        ]
      }
    ]
  })
}

const initLineChart = (data) => {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  const xAxis = data ? data.map((item) => item.date) : []
  const series = data ? data.map((item) => item.count) : []
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxis
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '学习次数',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        },
        lineStyle: { color: '#409eff' },
        itemStyle: { color: '#409eff' },
        data: series
      }
    ]
  })
}

const handleResize = () => {
  pieChart && pieChart.resize()
  lineChart && lineChart.resize()
}

const loadData = () => {
  dashboardData()
    .then((res) => {
      statCards[0].value = res.userCount || 0
      statCards[1].value = res.articleCount || 0
      statCards[2].value = res.videoCount || 0
      statCards[3].value = res.examCount || 0
      nextTick(() => {
        const pieData = (res.categoryContentCounts || []).map(item => ({
          value: item.totalCount || 0,
          name: item.categoryName || '未知'
        }))
        initPieChart(pieData)
        initLineChart(res.learningTrend || [])
      })
    })
    .catch((err) => {
      console.error('获取统计数据失败', err)
      // 使用空数据初始化图表
      nextTick(() => {
        initPieChart([])
        initLineChart([])
      })
    })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart && pieChart.dispose()
  lineChart && lineChart.dispose()
})
</script>

<style scoped lang="scss">
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 10px;

  .stat-card-body {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-info {
    flex: 1;
  }

  .stat-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 6px;
  }

  .stat-value {
    font-size: 26px;
    font-weight: bold;
    color: #303133;
  }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-box {
  height: 320px;
}
</style>

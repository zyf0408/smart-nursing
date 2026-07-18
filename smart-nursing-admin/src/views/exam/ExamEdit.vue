<template>
  <div class="app-container">
    <div class="card-box">
      <div class="edit-header">
        <el-page-header @back="goBack">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑考试' : '新增考试' }}</span>
          </template>
        </el-page-header>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="edit-form">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="form.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入考试描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number v-model="form.totalScore" :min="0" :max="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="及格分" prop="passScore">
              <el-input-number v-model="form.passScore" :min="0" :max="form.totalScore" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="duration">
              <el-input-number v-model="form.duration" :min="1" :max="300" style="width: 100%" />
              <span class="form-tip">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试次数" prop="maxAttempts">
              <el-input-number v-model="form.maxAttempts" :min="1" :max="99" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">关联试题</el-divider>
        <div class="question-toolbar">
          <el-button type="primary" :icon="Plus" @click="questionSelectVisible = true">添加试题</el-button>
          <span class="question-summary">已选 {{ form.questionIds.length }} 题，合计 {{ form.totalScore }} 分</span>
        </div>
        <el-table :data="selectedQuestions" border style="width: 100%">
          <el-table-column type="index" label="#" width="50" align="center" />
          <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
          <el-table-column prop="questionType" label="题型" width="100" align="center">
            <template #default="{ row }">
              <el-tag>{{ getQuestionTypeText(row.questionType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分值" width="80" align="center" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link :icon="Delete" @click="removeQuestion($index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-form-item style="margin-top: 20px">
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 试题选择弹窗 -->
    <el-dialog v-model="questionSelectVisible" title="选择试题" width="800px">
      <div class="search-bar">
        <el-input v-model="questionParams.content" placeholder="题目内容" clearable style="width: 200px" @keyup.enter="loadQuestions" />
        <el-select v-model="questionParams.questionType" placeholder="题型" clearable style="width: 120px">
          <el-option label="单选题" :value="1" />
          <el-option label="多选题" :value="2" />
          <el-option label="判断题" :value="3" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="loadQuestions">搜索</el-button>
      </div>
      <el-table
        ref="questionTableRef"
        :data="questionTableData"
        border
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ getQuestionTypeText(row.questionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" align="center" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="questionParams.pageNum"
          v-model:page-size="questionParams.pageSize"
          :total="questionTotal"
          layout="total, prev, pager, next"
          background
          @current-change="loadQuestions"
        />
      </div>
      <template #footer>
        <el-button @click="questionSelectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelectQuestions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Search } from '@element-plus/icons-vue'
import { examGetById, examAdd, examUpdate } from '@/api/exam'
import { questionList } from '@/api/question'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const submitLoading = ref(false)
const isEdit = computed(() => !!route.query.id)

const form = reactive({
  examId: null,
  examName: '',
  description: '',
  totalScore: 100,
  passScore: 60,
  duration: 60,
  maxAttempts: 1,
  startTime: '',
  endTime: '',
  questionIds: []
})

const rules = {
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

const selectedQuestions = ref([])

const getQuestionTypeText = (type) => {
  const map = { 1: '单选题', 2: '多选题', 3: '判断题' }
  return map[type] || '未知'
}

// 试题选择弹窗
const questionSelectVisible = ref(false)
const questionTableRef = ref()
const questionTableData = ref([])
const questionTotal = ref(0)
const questionParams = reactive({
  pageNum: 1,
  pageSize: 10,
  content: '',
  questionType: ''
})
const tempSelectedQuestions = ref([])

const loadQuestions = () => {
  questionList(questionParams)
    .then((res) => {
      questionTableData.value = res.records || res.list || []
      questionTotal.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取试题列表失败', err)
    })
}

const handleSelectionChange = (selection) => {
  tempSelectedQuestions.value = selection
}

const confirmSelectQuestions = () => {
  tempSelectedQuestions.value.forEach((q) => {
    if (!form.questionIds.includes(q.questionId)) {
      form.questionIds.push(q.questionId)
      selectedQuestions.value.push(q)
    }
  })
  // 更新总分
  form.totalScore = selectedQuestions.value.reduce((sum, q) => sum + (q.score || 0), 0)
  questionSelectVisible.value = false
}

const removeQuestion = (index) => {
  selectedQuestions.value.splice(index, 1)
  form.questionIds.splice(index, 1)
  form.totalScore = selectedQuestions.value.reduce((sum, q) => sum + (q.score || 0), 0)
}

const loadExam = () => {
  if (!route.query.id) return
  examGetById(route.query.id)
    .then((res) => {
      Object.assign(form, res)
      form.questionIds = res.questionIds || []
      selectedQuestions.value = res.questions || []
    })
    .catch((err) => {
      console.error('获取考试失败', err)
    })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    if (form.questionIds.length === 0) {
      ElMessage.warning('请至少添加一道试题')
      return
    }
    submitLoading.value = true
    const api = form.examId ? examUpdate(form) : examAdd(form)
    api
      .then(() => {
        ElMessage.success(form.examId ? '修改成功' : '新增成功')
        router.push('/exam/list')
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
  router.push('/exam/list')
}

onMounted(() => {
  loadExam()
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

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 13px;
}

.question-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;

  .question-summary {
    font-size: 14px;
    color: #606266;
  }
}
</style>

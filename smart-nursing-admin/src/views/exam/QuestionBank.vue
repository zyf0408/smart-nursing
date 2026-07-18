<template>
  <div class="app-container">
    <div class="card-box">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="queryParams.content"
          placeholder="题目内容"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.questionType" placeholder="题型" clearable style="width: 140px">
          <el-option label="单选题" :value="1" />
          <el-option label="多选题" :value="2" />
          <el-option label="判断题" :value="3" />
          <el-option label="解答题" :value="4" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="success" :icon="Plus" @click="handleAdd">新增试题</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.questionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80" align="center" />
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="题型" prop="questionType">
          <el-radio-group v-model="form.questionType" @change="handleTypeChange">
            <el-radio :value="1">单选题</el-radio>
            <el-radio :value="2">多选题</el-radio>
            <el-radio :value="3">判断题</el-radio>
            <el-radio :value="4">解答题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <!-- 选项（单选/多选） -->
        <template v-if="form.questionType === 1 || form.questionType === 2">
          <el-form-item
            v-for="(option, index) in form.options"
            :key="index"
            :label="`选项${getOptionLabel(index)}`"
          >
            <el-input v-model="option.content" placeholder="请输入选项内容" style="width: 80%; margin-right: 10px" />
            <el-button type="danger" link :icon="Delete" @click="removeOption(index)" v-if="form.options.length > 2" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" link :icon="Plus" @click="addOption">添加选项</el-button>
          </el-form-item>
          <el-form-item label="正确答案" prop="answer">
            <el-select v-if="form.questionType === 1" v-model="form.answer" placeholder="请选择正确选项">
              <el-option
                v-for="(option, index) in form.options"
                :key="index"
                :label="getOptionLabel(index)"
                :value="getOptionLabel(index)"
              />
            </el-select>
            <el-checkbox-group v-else v-model="form.answerList">
              <el-checkbox
                v-for="(option, index) in form.options"
                :key="index"
                :label="getOptionLabel(index)"
                :value="getOptionLabel(index)"
              />
            </el-checkbox-group>
          </el-form-item>
        </template>
        <!-- 判断题 -->
        <el-form-item label="正确答案" prop="answer" v-if="form.questionType === 3">
          <el-radio-group v-model="form.answer">
            <el-radio value="对">正确</el-radio>
            <el-radio value="错">错误</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 解答题：参考答案/评分要点（供AI评分） -->
        <el-form-item label="参考答案" prop="referenceAnswer" v-if="form.questionType === 4">
          <el-input v-model="form.referenceAnswer" type="textarea" :rows="5" placeholder="请输入参考答案/评分要点（供AI评分使用）" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="解析" prop="analysis">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="请输入解析" />
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
import { questionList, questionAdd, questionUpdate, questionDelete } from '@/api/question'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  content: '',
  questionType: ''
})

const getTypeText = (type) => {
  const map = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '解答题' }
  return map[type] || '未知'
}

const getOptionLabel = (index) => String.fromCharCode(65 + index)

const loadData = () => {
  loading.value = true
  questionList(queryParams)
    .then((res) => {
      tableData.value = res.records || res.list || []
      total.value = res.total || 0
    })
    .catch((err) => {
      console.error('获取试题列表失败', err)
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
  queryParams.content = ''
  queryParams.questionType = ''
  queryParams.pageNum = 1
  loadData()
}

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增试题')
const formRef = ref()
const form = reactive({
  questionId: null,
  questionType: 1,
  content: '',
  options: [{ content: '' }, { content: '' }],
  answer: '',
  answerList: [],
  referenceAnswer: '',
  score: 5,
  analysis: ''
})

const rules = {
  questionType: [{ required: true, message: '请选择题型', trigger: 'change' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }]
}

const handleTypeChange = () => {
  form.answer = ''
  form.answerList = []
  form.referenceAnswer = ''
  if (form.questionType === 1 || form.questionType === 2) {
    form.options = [{ content: '' }, { content: '' }]
  }
}

const addOption = () => {
  if (form.options.length >= 6) {
    ElMessage.warning('最多支持6个选项')
    return
  }
  form.options.push({ content: '' })
}

const removeOption = (index) => {
  form.options.splice(index, 1)
}

const handleAdd = () => {
  dialogTitle.value = '新增试题'
  Object.assign(form, {
    questionId: null,
    questionType: 1,
    content: '',
    options: [{ content: '' }, { content: '' }],
    answer: '',
    answerList: [],
    referenceAnswer: '',
    score: 5,
    analysis: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑试题'
  // 将后端的 optionA/B/C/D 映射为 options 数组
  const options = []
  if (row.optionA) options.push({ content: row.optionA })
  if (row.optionB) options.push({ content: row.optionB })
  if (row.optionC) options.push({ content: row.optionC })
  if (row.optionD) options.push({ content: row.optionD })
  Object.assign(form, {
    ...row,
    options: options.length > 0 ? options : [{ content: '' }, { content: '' }],
    answerList: row.answer ? row.answer.split(',') : []
  })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    const submitData = { ...form }
    // 多选题答案处理
    if (form.questionType === 2) {
      submitData.answer = form.answerList.join(',')
    }
    // 将 options 数组映射为 optionA/B/C/D（仅单选/多选）
    if ((form.questionType === 1 || form.questionType === 2) && form.options && form.options.length > 0) {
      submitData.optionA = form.options[0]?.content || ''
      submitData.optionB = form.options[1]?.content || ''
      submitData.optionC = form.options[2]?.content || ''
      submitData.optionD = form.options[3]?.content || ''
    }
    delete submitData.options
    delete submitData.answerList
    submitLoading.value = true
    const api = form.questionId ? questionUpdate(submitData) : questionAdd(submitData)
    api
      .then(() => {
        ElMessage.success(form.questionId ? '修改成功' : '新增成功')
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
  ElMessageBox.confirm('确定删除该试题吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      questionDelete(row.questionId)
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

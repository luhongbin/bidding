<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>知识库</h3>
      <el-form ref="knowledge" :rules="rules" :model="knowledge" label-width="150px">
        <el-form-item label="知识库名称" prop="title">
          <el-input v-model="knowledge.title" />
        </el-form-item>
        <el-form-item label="是否中文" prop="isNew">
          <el-radio-group v-model="knowledge.lang">
            <el-radio :label="true">中文</el-radio>
            <el-radio :label="false">英文</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否发布" prop="status">
          <el-radio-group v-model="knowledge.status">
            <el-radio :label="false">不发布</el-radio>
            <el-radio :label="true">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否常用" prop="isPopular">
          <el-radio-group v-model="knowledge.isPopular">
            <el-radio :label="true">常用</el-radio>
            <el-radio :label="false">常用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="title">
          <el-input v-model="knowledge.sortOrder" />
        </el-form-item>
        <el-form-item label="所属分类">
          <el-cascader :options="categoryList" expand-trigger="hover" clearable @change="handleCategoryChange" />
        </el-form-item>

        <el-form-item label="知识介绍">
          <editor v-model="knowledge.content" :init="editorInit" />
        </el-form-item>
      </el-form>
    </el-card>
    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handlePublish">上架</el-button>
    </div>

  </div>
</template>

<style>
.el-card {
  margin-bottom: 10px;
}

.el-tag + .el-tag {
  margin-left: 10px;
}

.input-new-keyword {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.avatar-uploader .el-upload {
  width: 145px;
  height: 145px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { publishKnowledge, listCatAndBrand } from '@/api/knowledge'
import { createStorage, uploadPath } from '@/api/storage'
import Editor from '@tinymce/tinymce-vue'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'

export default {
  name: 'GoodsCreate',
  components: { Editor },

  data() {
    return {
      uploadPath,
      categoryList: [],
      knowledge: { status: false, isPopular: true, lang: true },
      rules: {
        title: [{ required: true, message: '名称不能为空', trigger: 'blur' }]
      },
      editorInit: {
        language: 'zh_CN',
        height: 500,
        convert_urls: false,
        plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
        toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    this.init()
  },

  methods: {
    init: function() {
      listCatAndBrand().then(response => {
        this.categoryList = response.data.data.categoryList
      })
    },
    handleCategoryChange(value) {
      this.knowledge.knowledgeCatalogId = value[value.length - 1]
    },
    handleCancel: function() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({ path: '/knowledge/knowledge' })
    },
    handlePublish: function() {
      // const finalGoods = {
      //   knowledge: this.knowledge
      // }
      publishKnowledge(this.knowledge).then(response => {
        this.$notify.success({
          title: '成功',
          message: '创建成功'
        })
        this.$store.dispatch('tagsView/delView', this.$route)
        this.$router.push({ path: '/knowledge/knowledge' })
      }).catch(response => {
        MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
          confirmButtonText: '确定',
          type: 'error'
        })
      })
    }
  }
}
</script>

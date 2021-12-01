<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="200px">
      <el-card class="box-card">
        <h3>模板信息</h3>
        <!-- 发货对话框 -->
        <el-form-item label="使用模板" prop="modelName">
          <template slot-scope="scope">
            <el-tag> {{ formatModel(dataForm.modelName) }} </el-tag>
          </template>
        </el-form-item>
        <el-form-item label="报价单主题" prop="version">
          <label>
            <textarea v-model="dataForm.purchaserNote" maxlength="200" style="width: 70%;" placeholder="描述" />
          </label>
        </el-form-item>
      </el-card>
      <el-card v-show="rubberCardVisiable" class="box-card">
        <h3>塑料橡胶类商品信息</h3>
        <el-button type="primary" @click="rubberShowNew">新增</el-button>
        <el-table :data="detail">
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column property="id" label="单号" />
          <el-table-column property="code" label="品号" />
          <el-table-column property="name" label="品名" />
          <el-table-column property="spec" label="规格" />
          <el-table-column property="material" label="材质" />
          <el-table-column property="weight" label="理论重量" />
          <el-table-column property="quantityYear" label="年预估量" />
        </el-table>

        <el-dialog :visible.sync="rubberVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="产品名称">
              <span>(品号) {{ detailForm.code }} (品名) {{ detailForm.name }}</span>
              <span>(规格) {{ detailForm.spec }}</span>
              <el-button type="primary" @click="rubberShow">修改品号</el-button>
            </el-form-item>
            <el-form-item label="材质" prop="material">
              <el-input v-model="detailForm.material" class="input-width" />
            </el-form-item>
            <el-form-item label="理论重量" prop="weight">
              <el-input v-model="detailForm.weight" maxlength="10" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">g</template>
              </el-input>
            </el-form-item>
            <el-form-item label="年预估量" prop="quantityYear">
              <el-input v-model="detailForm.quantityYear" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">万PCS</template>
              </el-input>
            </el-form-item>
            <el-form-item label="备注">
              <editor v-model="detailForm.appendix" v-if="editDisplay" :init="editorInit"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="rubberVisiable = false">取消</el-button>
            <el-button v-if="detailAdd" type="primary" @click="handleAttributeAdd">确定</el-button>
            <el-button v-else type="primary" @click="detailEdit">确定</el-button>
          </div>
        </el-dialog>
      </el-card>
      <el-card v-show="electronicCardVisiable" class="box-card">
        <h3>电子电器类商品信息</h3>
        <el-button type="primary" @click="rubberShowNew">添加</el-button>
        <el-table :data="detail">
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column property="id" label="单号" />
          <el-table-column property="code" label="品号" />
          <el-table-column property="name" label="品名" />
          <el-table-column property="spec" label="规格" />
          <el-table-column property="quantityYear" label="年预估量" />
        </el-table>

        <el-dialog :visible.sync="electronicVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="产品名称">
              <span>(品号) {{ detailForm.code }} (品名) {{ detailForm.name }}</span>
              <span>(规格) {{ detailForm.spec }}</span>
              <el-button type="primary" @click="rubberShow">修改品号</el-button>
            </el-form-item>
            <el-form-item label="年预估量" prop="quantityYear">
              <el-input v-model="detailForm.quantityYear" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="明细">
              <editor v-model="detailForm.appendix" v-if="editDisplay" :init="editorInit"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="electronicVisiable = false">取消</el-button>
            <el-button v-if="detailAdd" type="primary" @click="handleAttributeAdd">确定</el-button>
            <el-button v-else type="primary" @click="detailEdit">确定</el-button>
          </div>
        </el-dialog>
      </el-card>
      <el-card v-show="hardwareCardVisiable" class="box-card">
        <h3>五金类商品信息</h3>
        <el-button type="primary" @click="rubberShowNew">添加</el-button>
        <el-table :data="detail">
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column property="id" label="单号" />
          <el-table-column property="code" label="品号" />
          <el-table-column property="name" label="品名" />
          <el-table-column property="spec" label="规格" />
          <el-table-column property="material" label="材质" />
          <el-table-column property="weight" label="产品理论重量(克)" />
          <el-table-column property="quantityYear" label="年预估量" />
        </el-table>

        <el-dialog :visible.sync="hardwareVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="产品名称">
              <span>(品号) {{ detailForm.code }} (品名) {{ detailForm.name }}</span>
              <span>(规格) {{ detailForm.spec }}</span>
              <el-button type="primary" @click="rubberShow">修改品号</el-button>
            </el-form-item>
            <el-form-item label="材质" prop="material">
              <el-input v-model="detailForm.material" maxlength="60" />
            </el-form-item>
            <el-form-item label="产品理论重量" prop="weight">
              <el-input v-model="detailForm.weight" maxlength="10" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="年预估量" prop="quantityYear">
              <el-input v-model="detailForm.quantityYear" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="明细">
              <editor v-model="detailForm.appendix" v-if="editDisplay" :init="editorInit" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="hardwareVisiable = false">取消</el-button>
            <el-button v-if="detailAdd" type="primary" @click="handleAttributeAdd">确定</el-button>
            <el-button v-else type="primary" @click="detailEdit">确定</el-button>
          </div>
        </el-dialog>
      </el-card>
      <el-card v-show="dieCastingCardVisiable" class="box-card">
        <h3>压铸模具类商品信息</h3>
        <el-button type="primary" @click="rubberShowNew">添加</el-button>
        <el-table :data="detail">
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
              <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column property="id" label="单号" />
          <el-table-column property="code" label="品号" />
          <el-table-column property="name" label="品名" />
          <el-table-column property="spec" label="规格" />
          <el-table-column property="size" label="产品尺寸(长宽高)" />
          <el-table-column property="weight" label="产品理论重量(克)" />
          <el-table-column property="quantityYear" label="年预估量" />
        </el-table>

        <el-dialog :visible.sync="dieCastingVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="产品名称">
              <span>(品号) {{ detailForm.code }} (品名) {{ detailForm.name }}</span>
              <span>(规格) {{ detailForm.spec }}</span>
              <el-button type="primary" @click="rubberShow">修改品号</el-button>
            </el-form-item>
            <el-form-item label="产品尺寸" prop="size">
              <el-input v-model="detailForm.size" class="input-width">
                <template slot="append">长宽高</template>
              </el-input>
            </el-form-item>
            <el-form-item label="产品理论重量" prop="weight">
              <el-input v-model="detailForm.weight" maxlength="10" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="年预估量" prop="quantityYear">
              <el-input v-model="detailForm.quantityYear" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">万只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="明细">
              <editor v-model="detailForm.appendix" v-if="editDisplay" :init="editorInit" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dieCastingVisiable = false">取消</el-button>
            <el-button v-if="detailAdd" type="primary" @click="handleAttributeAdd">确定</el-button>
            <el-button v-else type="primary" @click="detailEdit">确定</el-button>
          </div>
        </el-dialog>
      </el-card>

      <el-card class="box-card">
        <el-form-item v-if="dataForm.quoteModelExcel !== undefined && dataForm.quoteModelExcel !== null" label="上传询价单" prop="quoteModelExcel">
          <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize">
            <el-button style="margin-left: 10px;" size="small" type="success">重新上传询价单</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个,不超过20M</div>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="dataForm.quoteModelExcel === undefined || dataForm.quoteModelExcel === null" label="上传询价单" prop="quoteModelExcel">
          <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize">
            <el-button style="margin-left: 10px;" size="small" type="success">开始上传(上传之后 要按[确定]才会生效)</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个,不超过20M</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="选择询价供应商" prop="quoteSupplyCode">
          <el-select v-model="dataForm.quoteSupplyCode" multiple placeholder="请选择" clear @change="selectDuty($event)">
            <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />
          </el-select>
          <el-button style="float: outside;" size="mini" type="primary" @click="handleCreate()">选择供应商</el-button>
        </el-form-item>
        <!--        <el-form-item label="添加核价小组成员" prop="approveCode">-->
        <!--          <el-select v-model="dataForm.approveCode" disabled multiple placeholder="请选择" clear>-->
        <!--            <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />-->
        <!--          </el-select>-->
        <!--          <span class="info">有核价负责人 决定 是否提交 [核价小组]审核</span>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="核价负责人" prop="dutyCode">-->
        <!--          <el-select v-model="dataForm.dutyCode" disabled placeholder="请选择">-->
        <!--            <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />-->
        <!--          </el-select>-->
        <!--          <span class="info">每个类别的核价单，存在不同的核价负责人</span>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="终审人" prop="ceoCode">-->
        <!--          <el-select v-model="dataForm.ceoCode" disabled placeholder="请选择">-->
        <!--            <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />-->
        <!--          </el-select>-->
        <!--          <span class="info">默认是CEO,最后敲定价格</span>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="通知人" prop="notice">-->
        <!--          <el-select v-model="dataForm.noticeCode" multiple disabled placeholder="请选择">-->
        <!--            <el-option v-for="item in listAdmin" :key="item.value" :label="item.deptname" :value="item.value" />-->
        <!--          </el-select>-->
        <!--          <span class="info">价格确认后 通知的人员</span>-->
        <!--        </el-form-item>-->
      </el-card>
    </el-form>
    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
<!--      <el-button v-if="dialogStatus==='create'" type="primary" @click="createData">确定</el-button>-->
      <el-button type="primary" @click="updateData">确定</el-button>
    </div>
    <el-dialog :visible.sync="addCodeVisiable" title="查询产品">
      <div class="search">
        <el-input v-model="listQueryCode.code" class="filter-item" style="width: 100px;" placeholder="请输入品号" />
        <el-input v-model="listQueryCode.name" class="filter-item" style="width: 200px;" placeholder="请输入品名" />
        <el-input v-model="listQueryCode.spec" class="filter-item" style="width: 200px;" placeholder="请输入规格" />
        <el-select v-model="listQueryCode.price" class="filter-item" placeholder="是否核过价">
          <el-option v-for="(key, value) in statusOptions" :key="key" :label="key" :value="value" />
        </el-select>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="searchCode">查找</el-button>
        <el-table v-loading="CodelistLoading" :data="listCode" element-loading-text="正在查询中。。。" border fit highlight-current-row>
          <el-table-column align="center" label="品号" prop="imaal001" />
          <el-table-column align="center" label="品名" prop="imaal003" />
          <el-table-column align="center" label="规格" prop="imaal004" />
          <el-table-column align="center" label="核过价" prop="price" >
            <template slot-scope="scope">
              <el-tag :type="scope.row.price ? 'success' : 'error' ">{{ scope.row.price ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="confirmAdd(scope.row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQueryCode.page" :limit.sync="listQueryCode.limit" @pagination="searchCode" />
      </div>
    </el-dialog>
    <el-dialog :visible.sync="addSupplyVisiable" title="添加供应商">
      <div class="search">
        <el-input v-model="listPhoneQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机||供应商" />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
        <el-table v-loading="listLoading" :data="supplyList" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column align="center" label="单号" prop="id" />
          <el-table-column align="center" label="姓名" prop="name" />
          <el-table-column align="center" label="手机" prop="phone" />
          <el-table-column align="center" label="单位" prop="note" />
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addSupplyVisiable = false">取消</el-button>
        <el-button type="primary" @click="confirmCodeAdd">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<style>

.text-area textarea {
  width: 100%;
  margin: 0.75rem  0;
  border: none;
  outline: none;
  padding-left: 1.125rem;
  height: 6.5rem ;
}

.text-area textarea::-webkit-input-placeholder {  color: #9E9E9E;}

</style>
<script>
import { createStorage, uploadPath } from '@/api/storage'
import { readquote, createQuote, updateQuote, readCode, listSupply, find,deleteRubber, deleteElectronic, deleteHardware, deleteDieCasting } from '@/api/quote'
import { read } from '@/api/quotemodel'
import { getToken } from '@/utils/auth'
import Editor from '@tinymce/tinymce-vue'
import Notify from "../../../../litemall-wx/lib/vant-weapp/notify/notify";
import Pagination from '@/components/Pagination'
import {deleteTopic} from "@/api/topic"; // Secondary package based on el-pagination

export default {
  name: 'QuoteBill',
  components: { Editor, Pagination },

  data() {
    return {
      show: false,
      statusOptions: [
        {
          label: '已经核过价',
          value: 0
        },
        {
          label: '没有核过价',
          value: 1
        }
      ],
      uploadPath,
      editDisplay: false,
      total: 0,
      setstatus: 0,
      quoteDialogVisible: false,
      addSupplyVisiable: false,
      userid: 0,
      rubberCardVisiable: false,
      electronicCardVisiable: false,
      hardwareCardVisiable: false,
      dieCastingCardVisiable: false,
      rubberVisiable: false,
      electronicVisiable: false,
      hardwareVisiable: false,
      dieCastingVisiable: false,
      detailAdd: false,
      url: '',
      dataForm: [],
      current: [],
      purchaser: '',
      selectedlist: [],
      approveNote: '',
      attributeAdd: false,
      reQuote: [],
      approve: [],
      supply: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      fileList: [],
      ListQuote: [],
      detailForm: [],
      detail: [],
      supplyList: [],
      quoteModelId: '',
      CodelistLoading: false,
      modelId: 0,
      listCode: [],
      listQueryCode: {
        code: '1',
        name: '',
        spec: '',
        price: undefined,
        page: 1,
        limit: 10
      },
      listPhoneQuery: {
        name: "",
        page: 1,
        limit: 10,
        sort: 'add_time',
        order: 'desc'
      },
      Code: {
        code: undefined,
        name: undefined,
        spec: undefined
      },
      listQuery: {
        page: 1,
        limit: 10,
        phone: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      listLoading: true,
      dialogFormVisible: true,
      addCodeVisiable: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        fileList: [{ required: true, message: '必须长传询价单的Excel文件', trigger: 'blur' }],
        quoteSupplyCode: [{ required: true, message: '采购员必须选择报价的供应商', trigger: 'blur' }],
        ceoChoice: [{ required: true, message: 'ceo必须供应商', trigger: 'blur' }],
        dutyChoice: [{ required: true, message: '责任人必须选择供应商', trigger: 'blur' }],
        code: [{ required: true, message: '必须输入品号', trigger: 'blur' }],
        name: [{ required: true, message: '必须输入品名', trigger: 'blur' }]
        // mouldCharge: [{ required: true, message: '必须输入模具费', trigger: 'blur' }],
        // processingCostSingle: [{ required: true, message: '必须输入单个产品加工费', trigger: 'blur' }],
        // moldNumber: [{ required: true, message: '必须输入模穴数', trigger: 'blur' }],
        // looseCore: [{ required: true, message: '必须输入抽芯数', trigger: 'blur' }],
        // materialPrice: [{ required: true, message: '必须输入材料价', trigger: 'blur' }]
      },
      shipDialogVisible: false,
      downloadLoading: false,
      channels: [],
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false,
        plugins: [
          'advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'
        ],
        toolbar: [
          'searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample',
          'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'
        ],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData)
            .then(res => {
              success(res.data.data.url)
            })
            .catch(() => {
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
    this.getList()
  },
  mounted() {
    this.$set(this.listPhoneQuery, "name", "1")
    this.$set(this.listQueryCode, "code", "1")
  },

  methods: {
    getList() {
      this.listLoading = true
      if (this.$route.query.id == null) {
        return
      }
      const Id = this.$route.query.id

      // this.Editor.execCommand('mceAddControl',true,'editor_id');
      readquote(Id).then(response => {
        this.reload()

        console.log(response)
        let newArr = response.data.data.detail.filter(item => item.status !== 2);

        // let newArr = Object.assign({}, response.data.data.detail).filter((item, index,arr)=>{ return item.status !== 2 });
        this.quoteBills = Object.assign({}, response.data.data.quoteBills)
        this.detail = newArr
        this.dataForm = Object.assign({}, response.data.data.quoteBills)

        this.current = Object.assign({}, response.data.data.currentUser)
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        this.quoteModelId = response.data.data.quoteModelId
        sessionStorage.setItem('userid', this.current.id)
        this.trueCard()
      }).catch(() => { this.list = []; this.total = 0; this.$notify.error({ title: '失败', message: '询价单没取出来数据' }) })
      this.listLoading = false

    },
    uploadUrl: function(response) {
      console.log(JSON.stringify(response))

      this.dataForm.quoteModelExcel = response.data.url
    },
    reload() {
      this.editDisplay = false
      this.$nextTick(() => (this.editDisplay = true))
      this.detailForm.appendix
    },
    checkFileSize: function(file) {
      if (file.size > 20485760) {
        this.$notify.error('${file.name}文件大于20m，请选择小于20M大小的图片')
        return false
      }
      return true
    },
    handleCancel: function() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({ path: '/quoteManage/quotebill' })
    },
    handleSelectionChange(val) {
      this.selectedlist = val
    },
    selectSupply(val) {
      console.log(val)
      this.listLoading = false
      for (let i = 0; i < this.modelNameList.length; i++) {
        if (this.modelNameList[i]['value'] === val) {
          this.$set(this.dataForm, 'quoteSupplyCode', this.modelNameList[i].supply)
          this.$set(this.dataForm, 'approveCode', this.modelNameList[i].approveCode)
          this.$set(this.dataForm, 'dutyCode', this.modelNameList[i].duty)
          this.$set(this.dataForm, 'ceoCode', this.modelNameList[i].ceoCode)
          this.$set(this.dataForm, 'noticeCode', this.modelNameList[i].notice)
          this.$set(this.dataForm, 'ceoChoice', this.modelNameList[i].supply)
          this.$set(this.dataForm, 'dutyChoice', this.modelNameList[i].supply)
          this.$set(this.dataForm, 'preApprove', this.modelNameList[i].preApprove)
          this.detail = []
          this.trueCard()
          const modelId = this.dataForm.modelName
          read(modelId).then(response => {
            console.log(response)
            this.quoteModelId = response.data.data.appendix
          }).catch(() => { this.$notify.error({ title: '失败', message: '模板附件没取出来数据' }) })
        }
      }
    },
    handleFilter() {
      this.listLoading = true

      listSupply(this.listPhoneQuery).then(response => {
        console.log(response)
        this.total = response.data.data.total
        this.supplyList = response.data.data.list
        this.listLoading = false
      }).catch(() => {
        this.supplyList = []
        this.listLoading = false
      })
    },
    handleCreate() {
      this.listPhoneQuery = {
        name: "1",
        page: 1,
        limit: 10,
        sort: 'add_time',
        order: 'desc'
      }
      this.list = []
      this.selectedlist = []
      this.addSupplyVisiable = true
    },
    confirmCodeAdd() {
      const newGoodsIds = []
      const newGoodsList = []
      this.selectedlist.forEach(item => {
        const id = item.id
        let found = false
        this.dataForm.quoteSupplyCode.forEach(SupplyId => {
          if (id === SupplyId) {
            found = true
          }
        })
        if (!found) {
          newGoodsIds.push(id)
          newGoodsList.push(item)
        }
      })

      if (newGoodsIds.length > 0) {
        // this.supplyList = []
        this.dataForm.quoteSupplyCode = this.dataForm.quoteSupplyCode.concat(newGoodsIds)
        // this.supplyList = this.supplyList.concat(newGoodsList)
      }
      this.addSupplyVisiable = false
    },
    handleDelete(row) {
      for (var index = 0; index < this.quoteSupplyCode.quoteSupplyCode.length; index++) {
        if (row.id === this.quoteSupplyCode.quoteSupplyCode[index]) {
          this.quoteSupplyCode.quoteSupplyCode.splice(index, 1)
        }
      }
      for (var index2 = 0; index2 < this.supplyList.length; index2++) {
        if (row.id === this.supplyList[index2].id) {
          this.supplyList.splice(index2, 1)
        }
      }
    },
    formatAdmin(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    trueCard() {
      this.rubberCardVisiable = false
      this.dieCastingCardVisiable = false
      this.hardwareCardVisiable = false
      this.electronicCardVisiable = false
      const modelId = this.dataForm.modelName
      if (modelId === 3) { this.rubberCardVisiable = true }
      if (modelId === 4) { this.dieCastingCardVisiable = true }
      if (modelId === 5) { this.hardwareCardVisiable = true }
      if (modelId === 6) { this.electronicCardVisiable = true }
    },
    selectDuty(val) {
      this.$set(this.dataForm, 'dutyChoice', this.quote.dutyChoice)
    },
    selectCeo(val) {
      this.$set(this.quote, 'ceoChoice', this.quote.dutyChoice)
    },
    createData() {
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dataForm.adminId = sessionStorage.getItem('userid')
      this.dataForm.status = 0
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createQuote(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({ title: '成功', message: '新增询价单成功' })
            }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dataForm.adminId = sessionStorage.getItem('userid')
      this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.dataForm.modify = sessionStorage.getItem('userId')
      this.dataForm.isAb = true

      for (let i = 0; i < this.listAdmin.length; i++) {
        if (this.dataForm.quoteSupplyCode.indexOf(this.listAdmin[i].value) >= 0) {
          if (this.listAdmin[i].capacity.indexOf('A') < 0 && this.listAdmin[i].capacity.indexOf('B') < 0) {
            this.dataForm.isAb = false
          }
        }
      }
      const modelId = this.dataForm.modelName
      console.log('modelId:' + JSON.stringify(modelId))
      const quoteInOne = { quoteRubber: [], quoteDieCasting: [], quoteHardware: [], quoteElectronic: [], quoteBill: {}}

      if (modelId === 3) { quoteInOne.quoteRubber = quoteInOne.quoteRubber.concat(this.detail) }
      if (modelId === 4) { quoteInOne.quoteDieCasting = quoteInOne.quoteDieCasting.concat(this.detail) }
      if (modelId === 5) { quoteInOne.quoteHardware = quoteInOne.quoteHardware.concat(this.detail) }
      if (modelId === 6) { quoteInOne.quoteElectronic = quoteInOne.quoteElectronic.concat(this.detail) }
      quoteInOne.quoteBill = this.dataForm
      // this.$refs['dataForm'].validate(valid => {
      //   if (valid) {
      console.log('quoteinone:' )
      console.log('quoteinone:' + JSON.stringify(quoteInOne))
      updateQuote(quoteInOne)
        .then(() => {
          this.dialogFormVisible = false; this.$notify.success({ title: '成功', message: '更新成功' })
          this.$store.dispatch('tagsView/delView', this.$route)
          this.$router.push({ path: '/quoteManage/quotebill' })
        })
        .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
    // } else {
    //   this.$notify.error({ title: '失败', message: '验证失败' })
      //   }
      // })
    },
    confirmAdd(row) {
      this.addCodeVisiable = false
      if (this.dialogStatus === 'create' ) { this.detailForm = {}; this.detailForm.appendix = this.quoteModelId; }
      this.detailForm.code = row.imaal001
      this.detailForm.name = row.imaal003
      this.detailForm.spec = row.imaal004
      this.detailForm.isNew = row.price
      // alert(this.dialogStatus)
      if (this.dialogStatus === 'create') { this.handleFilterAdmin() } else {
        this.detailEdit()
        this.rubberVisiable = false
        this.electronicVisiable = false
        this.hardwareVisiable = false
        this.dieCastingVisiable = false
        // this.detailForm = Object.assign({}, row)
        const modelId = this.dataForm.modelName
        if (modelId === 3) { this.rubberVisiable = true }
        if (modelId === 4) { this.dieCastingVisiable = true }
        if (modelId === 5) { this.hardwareVisiable = true }
        if (modelId === 6) { this.electronicVisiable = true }
      }
    },
    handleFilterAdmin() {
      this.detailAdd = true
      this.rubberVisiable = false
      this.electronicVisiable = false
      this.hardwareVisiable = false
      this.dieCastingVisiable = false
      // this.detailForm = Object.assign({}, row)
      const modelId = this.dataForm.modelName
      if (modelId === 3) { this.rubberVisiable = true }
      if (modelId === 4) { this.dieCastingVisiable = true }
      if (modelId === 5) { this.hardwareVisiable = true }
      if (modelId === 6) { this.electronicVisiable = true }
      this.AppriveCardVisiable = true
      this.detailForm.appendix = this.quoteModelId
    },
    rubberShowNew() {
      this.dialogStatus = 'create'
      this.detailAdd = true
      this.addCodeVisiable = true
    },
    rubberShow() {
      this.dialogStatus = 'update'
      this.detailAdd = false
      this.addCodeVisiable = true
    },

    searchCode() {
      this.CodelistLoading = true
      readCode(this.listQueryCode).then(response => {
        this.listCode = response.data.data.list
        if (this.listCode.length>0) { this.total = parseInt(response.data.data.list[0].total) }
        // this.total = response.data.data.total
        this.CodelistLoading = false
      }).catch(() => {
        console.log('无数据')
        this.listCode = []
        this.total = 0
        this.CodelistLoading = false
      })
    },
    handleAttributeAdd() {
      this.reload()
      this.$refs['attributeForm'].validate(valid => {
        if (valid) {
          var index = this.detail.length - 1
          for (var i = 0; i < this.detail.length; i++) {
            const v = this.detail[i]
            if (v.code === this.detailForm.code) {
              this.$message({
                type: 'warning',
                message: '已经存在品号值:' + v.code
              })
              return
            }
            if (v.code  === 0 ) {
              return
            } else {
              index = i
            }
          }
          this.detail.splice(index + 1, 0, this.detailForm)
          // this.detail.unshift(this.detailForm)
          this.rubberVisiable = false
          this.electronicVisiable = false
          this.hardwareVisiable = false
          this.dieCastingVisiable = false
        }
      }).catch(response => { console.log(JSON.stringify(response)); this.$notify.error({ title: '失败', message: response.data.errmsg }) })
    },
    detailEdit() {
      this.$refs['attributeForm'].validate(valid => {
        if (valid) {
          this.falseVisiable()

          this.detailForm.updateTime = ''
          for (var i = 0; i < this.detail.length; i++) {
            const v = this.detail[i]
            if (v.id === this.detailForm.id) {
              this.detail.splice(i, 1, this.detailForm)
              break
            }
          }
        } else {
          this.$notify.error({ title: '失败', message: '验证失败' })
        }
      })
    },
    falseVisiable() {
      this.rubberVisiable = false
      this.electronicVisiable = false
      this.hardwareVisiable = false
      this.dieCastingVisiable = false
    },
    handleInput(e) {
      const a = e.key.replace(/[^\d]/g, '')
      if (!a) { e.preventDefault() }
    },
    trueVisiable() {
      const modelId = this.dataForm.modelName
      if (modelId === 3) { this.rubberVisiable = true }
      if (modelId === 4) { this.dieCastingVisiable = true }
      if (modelId === 5) { this.hardwareVisiable = true }
      if (modelId === 6) { this.electronicVisiable = true }
    },
    handleAttributeShow(row) {
      this.detailAdd = false
      this.dialogStatus = 'update'
      // this.detailForm = {}
      if (row.id) {
        this.reload()
        this.detailForm = Object.assign({}, row)
        this.detailForm.appendix = row.appendix
        // this.reload()
        // this.detail.appendix = row.appendix
        // find(row.id, this.dataForm.modelName)
        //   .then(response => {
        //     this.detailForm.appendix = response.data.data.detail.appendix
        //     alert(JSON.stringify(this.detailForm.appendix))
        //   }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        this.attributeAdd = false
      } else {
        this.detailForm = Object.assign({}, row)
        this.attributeAdd = true
      }
      this.trueVisiable()
    },
    handleAttributeDelete(row) {
      this.$confirm('您确定删除[' + row.code + ']吗？').then(_ => {
        const modelId = this.dataForm.modelName
        if (modelId === 3) {
          deleteRubber(row).then(response => { this.$notify.success({title: '成功', message: '删除询价产品成功'}); const index = this.detail.indexOf(row); this.detail.splice(index, 1)
          }).catch(response => { this.$notify.error({title: '失败', message: response.data.errmsg}) })
        }
        if (modelId === 4) {
          deleteDieCasting(row).then(response => { this.$notify.success({title: '成功', message: '删除询价产品成功'}); const index = this.detail.indexOf(row); this.detail.splice(index, 1)
          }).catch(response => { this.$notify.error({title: '失败', message: response.data.errmsg}) })
        }
        if (modelId === 5) {
          deleteHardware(row).then(response => { this.$notify.success({title: '成功', message: '删除询价产品成功'}); const index = this.detail.indexOf(row); this.detail.splice(index, 1)
          }).catch(response => { this.$notify.error({title: '失败', message: response.data.errmsg}) })
        }
        if (modelId === 6) {
          deleteElectronic(row).then(response => { this.$notify.success({title: '成功', message: '删除询价产品成功'}); const index = this.detail.indexOf(row); this.detail.splice(index, 1)
          }).catch(response => { this.$notify.error({title: '失败', message: response.data.errmsg}) })
        }
      })
    },
    formatModel(modelId) {
      for (let i = 0; i < this.modelNameList.length; i++) {
        if (modelId === this.modelNameList[i].value) {
          return this.modelNameList[i].label
        }
      }
      return ''
    },
    formatRole(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].deptname
        }
      }
      return ''
    },
    formatdd(roleId) {
      for (let i = 0; i < this.listAdmin.length; i++) {
        if (roleId === this.listAdmin[i].value) {
          return this.listAdmin[i].dd
        }
      }
      return ''
    }
  }
}
</script>


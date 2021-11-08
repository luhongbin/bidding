
<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="200px">
      <el-card v-show="rubberCardVisiable" class="box-card">
        <h3>塑料橡胶类商品信息</h3>
        <el-table :data="detail">
          <el-table-column property="id" label="id" sortable />
          <el-table-column property="quoteId" label="主表ID" sortable />
          <el-table-column property="allname" label="产品名称" sortable />
          <el-table-column property="weight" label="理论重量" />
          <el-table-column property="quantityYear" label="年预估量" />
          <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column property="deviceType" label="设备型号(注塑机)" />
          <el-table-column property="looseCore" label="抽芯数(一出几)" />
          <el-table-column property="materialPrice" label="材料价(元/克)" />
          <el-table-column property="moldNumber" label="模穴数(一出几)" />
          <el-table-column property="processingCostSingle" label="单个产品加工费(元/克)" />
          <el-table-column property="pieceWeight" label="单个产品克重价格(元/克)" />
          <el-table-column property="mouldCharge" label="模具费" />
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === 3 || scope.row.status === 5" type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="rubberVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="品名规格">
              <span>(品号) {{ detailForm.code }}    (品名) {{ detailForm.name }}    (规格) {{ detailForm.spec }}</span>
            </el-form-item>
            <el-form-item label="年预估量">
              <span>(理论重量) {{ detailForm.weight }} Kg    (年预估量) {{ detailForm.quantityYear }} 只</span>
            </el-form-item>
            <el-form-item label="设备型号" prop="deviceType">
              <el-input v-model="detailForm.deviceType" maxlength="100">
                <template slot="append">注塑机</template>
              </el-input>
            </el-form-item>
            <el-form-item label="抽芯数" prop="looseCore">
              <el-input v-model="detailForm.looseCore" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">一出几</template>
              </el-input>
            </el-form-item>
            <el-form-item label="材料价" prop="materialPrice">
              <el-input v-model="detailForm.materialPrice" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="模穴数" prop="moldNumber">
              <el-input v-model="detailForm.moldNumber" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">一出几</template>
              </el-input>
            </el-form-item>
            <el-form-item label="单个产品加工费" prop="processingCostSingle">
              <el-input v-model="detailForm.processingCostSingle" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="单个产品克重价格" prop="pieceWeight">
              <el-input v-model="detailForm.pieceWeight" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="模具费" prop="mouldCharge">
              <el-input v-model="detailForm.mouldCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
              <span class="info">模具费用总价 明细填写在备注中</span>
            </el-form-item>
            <el-form-item label="备注">
              <editor v-model="detailForm.appendix" :init="editorInit" />
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
        <el-table :data="detail">
          <el-table-column property="id" label="id" sortable />
          <el-table-column property="quoteId" label="主表ID" sortable />
          <el-table-column property="allname" label="产品名称" sortable />
          <el-table-column property="quantityYear" label="年预估量" />
          <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column property="price" label="含税价" />
          <el-table-column property="delivery" label="交期" />
          <el-table-column property="moq" label="MOQ" />
          <el-table-column property="mpq" label="MPQ" />
          <el-table-column property="packageSize" label="包装方式" />
          <el-table-column property="brand" label="品牌'" />
          <el-table-column property="certificate" label="证书情况" />
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === 3 || scope.row.status === 5" type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="electronicVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="品名规格">
              <span>(品号) {{ detailForm.code }}    (品名) {{ detailForm.name }}    (规格) {{ detailForm.spec }}</span>
              <span>(报价截止时间) {{ quote.deadDate }}</span>
            </el-form-item>
            <el-form-item label="年预估量">
              <span>(年预估量) {{ detailForm.quantityYear }} 只</span>
            </el-form-item>
            <el-form-item label="含税价" prop="price">
              <el-input v-model="detailForm.price" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
            <el-form-item label="MOQ" prop="moq">
              <el-input v-model="detailForm.moq" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="MPQ" prop="mpq">
              <el-input v-model="detailForm.mpq" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="包装方式" prop="packageSize" maxlength="160">
              <el-input v-model="detailForm.packageSize" class="input-width" />
            </el-form-item>
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="detailForm.brand" class="input-width">
                <template slot="append">元/克</template>
              </el-input>
            </el-form-item>
            <el-form-item label="证书情况" prop="certificate">
              <el-input v-model="detailForm.certificate" class="input-width" />
            </el-form-item>
            <el-form-item label="备注">
              <editor v-model="detailForm.appendix" :init="editorInit" />
            </el-form-item>
            <el-form-item label="状态">
              <template slot-scope="scope">
                <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
              </template>
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
        <el-table :data="detail">
          <el-table-column property="id" label="id" sortable />
          <el-table-column property="quoteId" label="主表ID" sortable />
          <el-table-column property="allname" label="产品名称" sortable />
          <el-table-column property="material" label="材质" />
          <el-table-column property="weight" label="产品理论重量(克)" />
          <el-table-column property="quantityYear" label="年预估量" />
          <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column property="materialCharge" label="材料价格/吨" />
          <el-table-column property="materialPerCharge" label="单个产品材料价" />
          <el-table-column property="processingCharge" label="加工费" />
          <el-table-column property="electroplateCharge" label="电镀费" />
          <el-table-column property="otherCharge" label="其它费用" />
          <el-table-column property="price" label="产品报价" />
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === 3 || scope.row.status === 5" type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="hardwareVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="品名规格">
              <span>(品号) {{ detailForm.code }}    (品名) {{ detailForm.name }}    (规格) {{ detailForm.spec }}     (材质) {{ detailForm.material }}</span>
            </el-form-item>
            <el-form-item label="年预估量">
              <span>(产品理论重量) {{ detailForm.weight }} 克       (年预估量)  {{ detailForm.quantityYear }}</span>
            </el-form-item>
            <el-form-item label="材料价格" prop="quantityYear">
              <el-input v-model="detailForm.materialCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/吨</template>
              </el-input>
            </el-form-item>
            <el-form-item label="单个产品材料价" prop="materialPerCharge">
              <el-input v-model="detailForm.materialPerCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
            <el-form-item label="加工费" prop="processingCharge">
              <el-input v-model="detailForm.processingCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元(剪板、冲压、攻牙等)</template>
              </el-input>
            </el-form-item>
            <el-form-item label="电镀费" prop="electroplateCharge">
              <el-input v-model="detailForm.electroplateCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="其它费用" prop="otherCharge">
              <el-input v-model="detailForm.otherCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
            <el-form-item label="产品报价" prop="price">
              <el-input v-model="detailForm.price" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元/只</template>
              </el-input>
            </el-form-item>
            <el-form-item label="模具费" prop="mouldCharge">
              <el-input v-model="detailForm.mouldCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
              <span class="info">五金件类产品 明细填写在备注中</span>
            </el-form-item>
            <el-form-item label="备注">
              <editor v-model="detailForm.appendix" :init="editorInit" />
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
        <el-table :data="detail">
          <el-table-column property="id" label="id" sortable />
          <el-table-column property="quoteId" label="主表ID" sortable />
          <el-table-column property="allname" label="产品名称" sortable />
          <el-table-column property="size" label="产品尺寸(长宽高)" />
          <el-table-column property="weight" label="产品理论重量(克)" />
          <el-table-column align="center" label="状态" prop="quoteStatus2Filter">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.status | quoteStatus2Filter }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column property="quantityYear" label="年预估量" />
          <el-table-column property="moldNumber" label="模穴数(一出几）" />
          <el-table-column property="looseCore" label="抽芯数(一出几)" />
          <el-table-column property="feedingMode" label="进料方式" />
          <el-table-column property="deviceType" label="压铸机吨位" />
          <el-table-column property="deadline" label="模具设计寿命" />
          <el-table-column property="moldTime" label="开模时间" />
          <el-table-column property="mouldCharge" label="模具费(万)" />
          <el-table-column property="note1" label="备注" />
          <el-table-column property="material" label="产品材料" />
          <el-table-column property="processingCharge" label="产品加工费" />
          <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === 3 || scope.row.status === 5" type="primary" size="mini" @click="handleAttributeShow(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog :visible.sync="dieCastingVisiable" :title="detailAdd ? '添加商品' : '编辑商品'">
          <el-form ref="attributeForm" :rules="rules" :model="detailForm" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
            <el-form-item label="品名规格">
              <span>(品号) {{ detailForm.code }}    (品名) {{ detailForm.name }}    (规格) {{ detailForm.spec }}</span>
            </el-form-item>
            <el-form-item label="年预估量">
              <span>(产品尺寸) {{ detailForm.size }} 长宽高  (产品理论重量) {{ detailForm.weight }} 克  (年预估量) {{ detailForm.quantityYear }} 克 </span>
            </el-form-item>
            <el-form-item label="模穴数" prop="moldNumber">
              <el-input v-model="detailForm.moldNumber" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">一出几</template>
              </el-input>
            </el-form-item>
            <el-form-item label="抽芯数" prop="looseCore">
              <el-input v-model="detailForm.looseCore" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">一出几</template>
              </el-input>
            </el-form-item>
            <el-form-item label="进料方式" prop="feedingMode">
              <el-input v-model="detailForm.feedingMode" class="input-width" />
            </el-form-item>
            <el-form-item label="压铸机吨位" prop="deviceType">
              <el-input v-model="detailForm.deviceType" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">吨</template>
              </el-input>
            </el-form-item>
            <el-form-item label="模具设计寿命" prop="looseCore">
              <el-input v-model="detailForm.deadline" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">年</template>
              </el-input>
            </el-form-item>
            <el-form-item label="开模时间" prop="moldTime">
              <el-input v-model="detailForm.moldTime" class="input-width" />
            </el-form-item>
            <el-form-item label="模具费" prop="mouldCharge">
              <el-input v-model="detailForm.mouldCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">万元</template>
              </el-input>
            </el-form-item>
            <el-form-item label="备注" prop="note1">
              <el-input v-model="detailForm.note1" class="input-width" />
            </el-form-item>
            <el-form-item label="产品材料" prop="material">
              <el-input v-model="detailForm.material" class="input-width" />
            </el-form-item>
            <el-form-item label="产品加工费" prop="deadline">
              <el-input v-model="detailForm.processingCharge" type="number" class="input-width" @keydown="handleInput">
                <template slot="append">元</template>
              </el-input>
              <span class="info">模具费用总价 明细填写在备注中</span>
            </el-form-item>
            <el-form-item label="备注">
              <editor v-model="detailForm.appendix" :init="editorInit" />
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
        <h3>上传附件</h3>
        <el-form-item v-if="dataForm.requoteExcel !== '' && dataForm.requoteExcel !== null" label="上传报价单" prop="requoteExcel">
          <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize" accept=".xlsx">
            <el-button style="margin-left: 10px;" size="small" type="success">重新上传报价单</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个xlsx文件，且不超过20M</div>
          </el-upload>
        </el-form-item>

        <el-form-item v-if="dataForm.requoteExcel === '' || dataForm.requoteExcel === null" label="上传报价单" prop="requoteExcel">
          <el-upload :headers="headers" :limit="1" :action="uploadPath" :on-success="uploadUrl" :file-list="fileList" :before-upload="checkFileSize" accept=".xlsx">
            <el-button style="margin-left: 10px;" size="small" type="success">开始上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个xlsx文件，且不超过20M</div>
          </el-upload>
        </el-form-item>
        <label>
          <textarea v-model="dataForm.note" maxlength="200" style="width: 100%;" placeholder="备注" />
        </label>
      </el-card>
    </el-form>
    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="updateData">保存</el-button>
      <el-button type="primary" @click="handleSubmit">确定提交</el-button>
    </div>
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
import { getToken } from '@/utils/auth'
import Editor from '@tinymce/tinymce-vue'
import { myRead, updateRequote as updateQuote } from '@/api/requote'
import { find, submitQuote } from '@/api/quote'
import _ from 'lodash'
const statusMap2 = {
  0: '中标',
  1: '未中标',
  2: '重新报价',
  3: '未报价',
  4: '流标',
  5: '报价',
  6: '提交报价'
}
const statusMap3 = {
  0: '选中',
  1: '放弃'
}
export default {
  name: 'QuoteBill',
  components: { Editor },
  filters: {
    quoteStatus2Filter(status) {
      return statusMap2[status]
    },
    quoteStatus3Filter(status) {
      return statusMap3[status]
    }
  },
  data() {
    return {
      show: false,
      uploadPath,
      setstatus: 0,
      quoteDialogVisible: false,
      userid: 0,
      DialogVisiable: false,
      detailDialogVisible: false,
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
      approveNote: '',
      reQuote: [],
      approve: [],
      supply: [],
      listAdmin: [],
      uploadList: [],
      modelNameList: [],
      fileList: [],
      ListQuote: [],
      detailForm: [],
      approverow: [],
      detail: [],
      modelId: 0,
      listLoading: true,
      dialogFormVisible: true,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        fileList: [{ required: true, message: '必须长传询价单的Excel文件', trigger: 'blur' }],
        quoteSupplyCode: [{ required: true, message: '采购员必须选择报价的供应商', trigger: 'blur' }],
        ceoChoice: [{ required: true, message: 'ceo必须供应商', trigger: 'blur' }],
        price: [{ required: true, message: '必须价格', trigger: 'blur' }],
        code: [{ required: true, message: '必须输入品号', trigger: 'blur' }],
        name: [{ required: true, message: '必须输入品名', trigger: 'blur' }],
        mouldCharge: [{ required: true, message: '必须输入模具费', trigger: 'blur' }],
        processingCharge: [{ required: true, message: '必须输入产品加工费', trigger: 'blur' }],
        processingCostSingle: [{ required: true, message: '必须输入单个产品加工费', trigger: 'blur' }],
        moldNumber: [{ required: true, message: '必须输入模穴数', trigger: 'blur' }],
        looseCore: [{ required: true, message: '必须输入抽芯数', trigger: 'blur' }],
        materialPrice: [{ required: true, message: '必须输入材料价', trigger: 'blur' }]
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
  },

  methods: {
    getList() {
      if (this.$route.query.id == null) {
        return
      }
      const Id = this.$route.query.id
      this.approverow=this.$route.query.row
      myRead(Id).then(response => {
        // console.log('myRead:' + JSON.stringify(response))
        this.current = Object.assign({}, response.data.data.currentUser)
        let newArr = response.data.data.redetail.filter(item => item.status !== 2);
        this.detail = newArr
        // this.detail = response.data.data.redetail
        this.dataForm = response.data.data.reQuote
        this.listAdmin = response.data.data.optionsAdmin
        this.modelNameList = response.data.data.quoteModel
        this.quote = response.data.data.Quote
        this.approve = response.data.data.ApproveInfoList
        const modelId = response.data.data.Quote.modelName
        if (modelId === 3) { this.rubberCardVisiable = true }
        if (modelId === 4) { this.dieCastingCardVisiable = true }
        if (modelId === 5) { this.hardwareCardVisiable = true }
        if (modelId === 6) { this.electronicCardVisiable = true }
        this.detail.forEach(item => {
          item.allname = item.code + ':' + item.name + ':' + item.spec
        })
        sessionStorage.setItem('userid', this.current.id)
      }).catch(() => { this.list = []; this.total = 0; this.$notify.error({ title: '失败', message: '基础数据没取出来数据' }) })
    },
    trueCard(modelId) {
      this.rubberCardVisiable = false
      this.dieCastingCardVisiable = false
      this.hardwareCardVisiable = false
      this.electronicCardVisiable = false
      if (modelId === 3) { this.rubberCardVisiable = true }
      if (modelId === 4) { this.dieCastingCardVisiable = true }
      if (modelId === 5) { this.hardwareCardVisiable = true }
      if (modelId === 6) { this.electronicCardVisiable = true }
    },
    uploadUrl: function(response) {
      console.log(JSON.stringify(response))

      this.dataForm.requoteExcel = response.data.url
    },
    checkFileSize: function(file) {
      if (file.size > 20485760) {
        this.$notify.error('${file.name}文件大于20m，请选择小于20M大小的图片')
        return false
      }
      return true
    },

    handleInput(e) {
      const a = e.key.replace(/[^\d]/g, '')
      if (!a) { e.preventDefault() }
    },
    handleUpdate(row) {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.dataForm = Object.assign({}, row)
          this.dataForm.adminId = sessionStorage.getItem('userid')
          this.dataForm.purchaser = this.formatAdmin(this.dataForm.adminId)
          this.dialogStatus = 'update'
          this.dialogFormVisible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate()
          }) .catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        }
      })
    },
    updateData() {
      this.dataForm.modify = sessionStorage.getItem('userId')
      const quoteInOne = { quoteRubber: [], quoteDieCasting: [], quoteHardware: [], quoteElectronic: [], quoteBill: {}, reQuote: {}}
      quoteInOne.reQuote = this.dataForm
      const modelId = this.quote.modelName
      console.log('this.dataForm:' + JSON.stringify(this.dataForm))
      if (modelId === 3) { quoteInOne.quoteRubber = quoteInOne.quoteRubber.concat(this.detail) }
      if (modelId === 4) { quoteInOne.quoteDieCasting = quoteInOne.quoteDieCasting.concat(this.detail) }
      if (modelId === 5) { quoteInOne.quoteHardware = quoteInOne.quoteHardware.concat(this.detail) }
      if (modelId === 6) { quoteInOne.quoteElectronic = quoteInOne.quoteElectronic.concat(this.detail) }
      quoteInOne.quoteBill = this.quote
      console.log('quoteinone:' + JSON.stringify(quoteInOne))
      updateQuote(quoteInOne)
        .then(() => { this.$notify.success({ title: '成功', message: '更新成功' })
          // this.$store.dispatch('tagsView/delView', this.$route)
          // this.dialogFormVisible = false
          // this.$router.push({ path: '/supplyManage/requote' })
        })
    },
    handleCancel: function() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({ path: '/supplyManage/requote' })
    },
    rubberShow(row) {
      this.detailForm = Object.assign({}, row)
      this.detailAdd = true
      this.rubberVisiable = true
    },
    falseVisiable() {
      this.rubberVisiable = false
      this.electronicVisiable = false
      this.hardwareVisiable = false
      this.dieCastingVisiable = false
    },
    detailEdit() {
      this.$refs['attributeForm'].validate(valid => {
        if (valid) {
          this.detailForm.updateTime = ''
          for (var i = 0; i < this.detail.length; i++) {
            const v = this.detail[i]
            if (v.id === this.detailForm.id) {
              this.detail.splice(i, 1, this.detailForm)
              break
            }
          }
          this.falseVisiable()
        } else {
          this.$notify.error({ title: '失败', message: '验证失败' })
        }
      })
    },
    handleSubmit() {
      this.updateData()
      this.$store.dispatch('tagsView/delView', this.$route)
      this.dialogFormVisible = false
      this.$router.push({ path: '/supplyManage/requote-approve', query: { row: this.approverow }})
    },

    handleAttributeShow(row) {
      // const modelId = this.quote.modelName
      // this.detailForm = {}
      if (row.id) {
        this.detailForm = Object.assign({}, row)
        // find(row.id, modelId)
        //   .then(response => {
        //     this.detailForm = response.data.data.detail
        //     this.detailForm.allname = this.detailForm.code + ':' + this.detailForm.name + ':' + this.detailForm.spec
        //     console.log(JSON.stringify(response))
        //   }).catch(response => { this.$notify.error({ title: '失败', message: response.data.errmsg }) })
        this.attributeAdd = false
      } else {
        // this.detailForm = {}
        this.attributeAdd = true
      }
      this.trueVisiable()
    },
    trueVisiable() {
      const modelId = this.quote.modelName
      if (modelId === 3) { this.rubberVisiable = true }
      if (modelId === 4) { this.dieCastingVisiable = true }
      if (modelId === 5) { this.hardwareVisiable = true }
      if (modelId === 6) { this.electronicVisiable = true }
    },
    handleAttributeDelete(row) {
      const index = this.detail.indexOf(row)
      this.detail.splice(index, 1)
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


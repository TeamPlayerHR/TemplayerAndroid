package com.cts.teamplayer.fragments

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.cts.teamplayer.R
import com.cts.teamplayer.activities.WebViewActivity
import com.cts.teamplayer.adapters.PlanListAdapter
import com.cts.teamplayer.models.*
import com.cts.teamplayer.network.ApiClient
import com.cts.teamplayer.network.CheckNetworkConnection
import com.cts.teamplayer.network.ItemClickListner
import com.cts.teamplayer.util.MyConstants
import com.cts.teamplayer.util.MyConstants.PAYPAL_CLICK_REQUEST_CODE
import com.cts.teamplayer.util.TeamPlayerSharedPrefrence
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.tv_title_header
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_brief_questionnaire.*
import kotlinx.android.synthetic.main.fragment_brief_questionnaire.view.*
import kotlinx.android.synthetic.main.fragment_demo_group.*
import kotlinx.android.synthetic.main.fragment_invite_group_list.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.util.*

class BriefQuestionnaireFragment : Fragment(), View.OnClickListener, ItemClickListner {
    lateinit var v: View
    private var mpref: TeamPlayerSharedPrefrence? = null
    var planList: java.util.ArrayList<PlanList>? = null
    var REQUEST_CODE = 11
    var dialog1:Dialog?=null
    var positioninplan:Int?=null
    var stringNonceNew:String=""
    var whichplan:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_brief_questionnaire, container, false)
        mpref = TeamPlayerSharedPrefrence.getInstance(activity!!)
        v.btn_puchase_full_que.setOnClickListener(this)
        groupList()
        PerQuestionPrice()
        if(TeamPlayerSharedPrefrence.getInstance(activity!!).getFullQuestion("")!!.equals("false")){
            v.tv_plan.visibility=View.VISIBLE
            newUserList()
        }

        return v
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_puchase_full_que -> {
                /* val i = Intent(activity, WebViewActivity::class.java)
                     .putExtra("activity", "question").putExtra("url","https://dev.teamplayerhr.com/purchase")
                 startActivity(i)*/
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://dev.teamplayerhr.com/purchase"))
                startActivity(i)
            }
        }

    }
    private fun groupList(){
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<PlanListResponse>? = null//apiInterface.profileImage(body,token);
            call = apiInterface!!.plabListParameter(mpref!!.getAccessToken(""))
            call.enqueue(object : Callback<PlanListResponse> {
                override fun onResponse(
                    call: Call<PlanListResponse>,
                    response: retrofit2.Response<PlanListResponse>
                ) {
                    Log.e("log", response.body().toString());
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        try {
                            planList = response.body()!!.data as ArrayList<PlanList>?

                            setGroupList()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (response.code() == 500) {
                        Toast.makeText(
                            activity!!,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                activity!!,
                                "Some error occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }

                override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }


    fun setGroupList(){
        val manager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        v.recycler_plan_list.layoutManager = manager
        val   groupListAdapter =  PlanListAdapter(activity!!, planList, this)
        v.recycler_plan_list.adapter = groupListAdapter
    }

    fun setNewUserPlanList(){
        val manager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        v.recycler_new_user_plan_list.layoutManager = manager
        val   groupListAdapter =  PlanListAdapter(activity!!, planList, this)
        v.recycler_new_user_plan_list.adapter = groupListAdapter
    }

    override fun onClickItem(position: Int, requestcode: Int) {
        if(requestcode==PAYPAL_CLICK_REQUEST_CODE){
            positioninplan=position
            whichplan="olduser"
            if(edit_number_of_participants.text.toString().trim().isEmpty()){
                Toast.makeText(activity, getString(R.string.enter_no_of_question), Toast.LENGTH_SHORT).show()
            }else if (edit_number_of_participants.text.toString().trim().equals("0")){
                Toast.makeText(activity, getString(R.string.enter_no_of_question), Toast.LENGTH_SHORT).show()
            }
            else{
                getbraintreeTokenApi()
            }

        }
        if(requestcode== MyConstants.NEW_PAYPAL_CLICK_REQUEST_CODE){
            whichplan="newuser"
            positioninplan=position
            getbraintreeTokenApi()
        }
    }
    var PerQuestionPriceResponseDataItem: java.util.ArrayList<PerQuestionPriceResponseDataItem>? = null
    private fun PerQuestionPrice(){
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<PerQuestionPriceResponse>? = null//apiInterface.profileImage(body,token);
            call = apiInterface!!.getDemoPlan()
            call!!.enqueue(object : Callback<PerQuestionPriceResponse> {
                override fun onResponse(
                    call: Call<PerQuestionPriceResponse>,
                    response: retrofit2.Response<PerQuestionPriceResponse>
                ) {
                    Log.e("log", response.body().toString());
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        try {
                            PerQuestionPriceResponseDataItem = response.body()!!.data as ArrayList<PerQuestionPriceResponseDataItem>?


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (response.code() == 500) {
                        Toast.makeText(
                            activity!!,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                activity!!,
                                "Some error occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }

                override fun onFailure(call: Call<PerQuestionPriceResponse>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
    var brintreeToken=""
    var orderId=""
    private fun getbraintreeTokenApi() {
        //   Log.e("json",jsonObject.toString())
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(this.resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<JsonObject>?
            call = apiInterface!!.getclienttokenParameter(
                TeamPlayerSharedPrefrence.getInstance(
                    activity!!
                ).getAccessToken("")
            )
            call!!.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    Log.e("response111", response.body().toString());
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        try {
                            val jsonObject = JSONObject(response.body().toString())
                            /* "{
                             ""orderId"" : ""ORD12345"",
                             ""brainTreeToken"" : ""token123"",
                             ""success"" : true
                         }"*/

                            //    orderId = jsonObject.optString("orderId").toString()
                            brintreeToken = jsonObject.optString("token").toString()

                            val dropInRequest =
                                DropInRequest().clientToken(brintreeToken).collectDeviceData(
                                    true
                                )
                            startActivityForResult(
                                dropInRequest.getIntent(activity!!),
                                REQUEST_CODE
                            )

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result: DropInResult? = data!!.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                val nonce = result!!.paymentMethodNonce
                val stringNonce = nonce!!.nonce
                stringNonceNew=stringNonce

                if(whichplan!!.equals("newuser")){
                    val paymentNewUpdateRequest: JsonObject = JsonObject()
                    paymentNewUpdateRequest.addProperty("id",planList!!.get(positioninplan!!).id )
                    paymentNewUpdateRequest.addProperty("transaction_id", stringNonceNew)
                    updateNewUserPayment(paymentNewUpdateRequest)

                }else{

                    val x=v.edit_number_of_participants.text!!.trim()
// Assign two BigDecimal objects

                    // Assign two BigDecimal objects
                    val b1 = BigDecimal(v.edit_number_of_participants.text!!.trim().toString())
                    val b2 = BigDecimal(PerQuestionPriceResponseDataItem!!.get(0).amount)

                    // Multiply b1 with b2 and assign result to b3

                    // Multiply b1 with b2 and assign result to b3
                    val b3: BigDecimal = b1.multiply(b2)
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("nonce", stringNonce)
                    jsonObject.addProperty(
                        "chargeAmount",
                        b3
                    )

                    Log.e("json", jsonObject.toString())
                    authenticatePaymentApi(jsonObject)
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.e("mylog", "user canceled")
            } else {
                // handle errors here, an exception may be available in
                val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR)

                Log.e("error", error.toString())

            }
        }

    }
    private fun authenticatePaymentApi(jsonObject: JsonObject) {
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(this.resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<JsonObject>?
            call = apiInterface!!.authenticatePayment(
                TeamPlayerSharedPrefrence.getInstance(activity!!).getAccessToken(
                    ""
                ), jsonObject
            )
            call!!.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        var jsonObject = JSONObject(response.body()!!.toString())
                        try {
                            Log.d("response", response.body()!!.toString())
                            Toast.makeText(
                                activity!!,
                                "Payment done sucessfully",
                                Toast.LENGTH_LONG
                            ).show()


                            val paymentUpdateRequest: JsonObject = JsonObject()
                            //   paymentUpdateRequest.addProperty("id",planList!!.get(positioninplan!!).id )
                            paymentUpdateRequest.addProperty("id","1")
                            paymentUpdateRequest.addProperty("number_survay", v.edit_number_of_participants.text!!.trim().toString())
                            paymentUpdateRequest.addProperty("data", "")

                            /*   val paymentNewUpdateRequest: JsonObject = JsonObject()
                               paymentNewUpdateRequest.addProperty("id",planList!!.get(positioninplan!!).id )
                               paymentNewUpdateRequest.addProperty("transaction_id", stringNonceNew)
   */
                            upDateDemoPaymentApi(paymentUpdateRequest)
                            //   updateNewUserPayment(paymentNewUpdateRequest)
                            edit_number_of_participants.text!!.clear()
                            //    Toast.makeText(activity!!, jsonObject.optString("message"), Toast.LENGTH_LONG).show()
                            /*  var cartTable = dbController!!.getCartInfo("")
                            cartTable.clear()*/

                            /*  startActivity(
                                Intent(activity!!, OrderDoneActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK))*/

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
    private fun upDateDemoPaymentApi(jsonObject: JsonObject) {
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(this.resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<JsonObject>?
            call = apiInterface!!.getUpdateupdateDemoPayment(
                TeamPlayerSharedPrefrence.getInstance(activity!!).getAccessToken(""), jsonObject
            )
            call!!.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        var jsonObject = JSONObject(response.body()!!.toString())
                        try {
                            Log.d("response", response.body()!!.toString())

                            dialog1 = Dialog(activity!!)
                            dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog1!!.setCancelable(false)
                            dialog1!!.setContentView(R.layout.dialog_question_purchase_done)
                            dialog1!!.setCanceledOnTouchOutside(true)
                            dialog1!!.window!!.setLayout(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT
                            )
                            dialog1!!.window!!.setBackgroundDrawable(ColorDrawable(activity!!.resources.getColor(R.color.full_transparent)))

                            dialog1!!.window!!.setGravity(Gravity.CENTER)

                            val rl_yes_que_purchase = dialog1!!.findViewById(R.id.rl_yes_que_purchase) as RelativeLayout
                            val rl_not_now_app_ques = dialog1!!.findViewById(R.id.rl_not_now_app_ques) as RelativeLayout

                            dialog1!!.show()
                            rl_not_now_app_ques.setOnClickListener {
                                dialog1!!.dismiss()
                            }
                            rl_yes_que_purchase.setOnClickListener {
                                dialog1!!.dismiss()
                                activity!!.tv_title_header.text="App Questionnaire"
                                val  homeFragment = AppQuestionnaireFragment()
                                val manager = activity!!.supportFragmentManager
                                val transaction = manager.beginTransaction()
                                transaction.replace(R.id.container, homeFragment)
                                // transaction.addToBackStack(null);
                                transaction.commit()
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun updateNewUserPayment(jsonObject: JsonObject) {
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(this.resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<JsonObject>?
            call = apiInterface!!.updateNewUserPayment(
                TeamPlayerSharedPrefrence.getInstance(activity!!).getAccessToken(""), jsonObject
            )
            call!!.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        var jsonObject = JSONObject(response.body()!!.toString())
                        try {
                            Log.d("response", response.body()!!.toString())
                            Toast.makeText(activity!!, jsonObject.optString("message"), Toast.LENGTH_LONG).show()


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
    private fun newUserList(){
        if (CheckNetworkConnection.isConnection1(activity!!, true)) {
            val progress = ProgressDialog(activity!!)
            progress.setMessage(resources.getString(R.string.please_wait))
            progress.setCancelable(false)
            progress.isIndeterminate = true
            progress.show()
            val apiInterface = ApiClient.getConnection(activity!!)
            var call: Call<PlanListResponse>? = null//apiInterface.profileImage(body,token);
            call = apiInterface!!.newuserplanParameter(mpref!!.getAccessToken(""))
            call.enqueue(object : Callback<PlanListResponse> {
                override fun onResponse(
                    call: Call<PlanListResponse>,
                    response: retrofit2.Response<PlanListResponse>
                ) {
                    Log.e("log", response.body().toString());
                    progress.dismiss()
                    if (response.code() >= 200 && response.code() < 210) {
                        try {
                            planList = response.body()!!.data as ArrayList<PlanList>?

                            setNewUserPlanList()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (response.code() == 500) {
                        Toast.makeText(
                            activity!!,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        var reader: BufferedReader? = null
                        val sb = StringBuilder()
                        try {
                            reader = BufferedReader(
                                InputStreamReader(
                                    response.errorBody()!!.byteStream()
                                )
                            )
                            var line = reader.readLine()
                            try {
                                if (line != null) {
                                    sb.append(line)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            val finallyError = sb.toString()
                            val jsonObjectError = JSONObject(finallyError)
                            val message = jsonObjectError.optString("message")
                            Toast.makeText(activity!!, message, Toast.LENGTH_LONG).show()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                activity!!,
                                "Some error occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }

                override fun onFailure(call: Call<PlanListResponse>, t: Throwable) {
                    progress.dismiss()
                    Toast.makeText(
                        activity!!,
                        resources.getString(R.string.Something_went_worng),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                activity!!,
                resources.getString(R.string.please_check_internet),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }




}
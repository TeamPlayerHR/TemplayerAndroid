package com.cts.teamplayer.models

import com.google.gson.annotations.SerializedName

data class CompatibilitySurveyResponse(

	@field:SerializedName("data")
	val data: CompatibilitySurveyData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

/*data class CompatibilitySurveySectionListItemItem(

	@field:SerializedName("score")
	val score: String? = null,

	@field:SerializedName("section_name")
	val sectionName: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)*/

data class CompatibilitySurveyData(

	/*@field:SerializedName("section_list")
	val sectionList: List<CompatibilitySurveySectionListItemItem?>? = null,*/

	@field:SerializedName("user_list")
	val userList: List<CompatibilitySurveyUserListItem?>? = null,

	@field:SerializedName("team")
	val team: CompatibilitySurveyTeam? = null,

	@field:SerializedName("user")
	val user: CompatibilitySurveyUser? = null
)

data class CompatibilitySurveyUser(

	@field:SerializedName("zip")
	val zip: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("occupation")
	val occupation: String? = null,

	@field:SerializedName("im")
	val im: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("no_of_employees")
	val noOfEmployees: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("organization_name")
	val organizationName: Any? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("user_role")
	val userRole: Any? = null,

	@field:SerializedName("cv")
	val cv: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("sector")
	val sector: Any? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class CompatibilitySurveyUserListItem(

	@field:SerializedName("score")
	val score: Int? = null,

	@field:SerializedName("im")
	val im: String? = null,

	@field:SerializedName("section_result")
	val sectionResult: List<Any?>? = null,

	@field:SerializedName("max_score")
	val maxScore: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)

data class CompatibilitySurveyTeam(

	@field:SerializedName("name")
	val name: String? = null
)

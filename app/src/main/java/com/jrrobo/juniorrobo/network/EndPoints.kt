package com.jrrobo.juniorrobo.network

class EndPoints {

    companion object {

        // Base URL provided
        const val BASE_URL: String = "http://karan17-001-site1.gtempurl.com/api/"

        // Endpoint for requesting OTP
        const val APP_SMS: String = "appSms"

        // Endpoint for requesting and getting response of OTP
        const val APP_LOGIN: String = "appLogin"

        // Endpoint for updating the profile of the student and also used for GET request to fetch student profile data
        const val APP_STUDENT: String = "appStudent"

        // Endpoint for posting the question asked by the user to backend
        const val APP_QUESTION: String = "appQuestion"

        // Endpoint for getting all the categories of the question
        const val APP_CATEGORY: String = "appCategory"

        // Endpoint for POST request of an answer for a question
        const val APP_QUE_ANS: String = "appQueAns"

        // Endpoint for uploading images and files
        const val FILE_UPLOAD: String = "FileUpload/UploadImage"
    }

}
package com.jrrobo.juniorroboapp.network

class EndPoints {

    companion object {

        // Base URL provided
        const val BASE_URL: String = "https://jrrobo.com/api/"

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
        const val IMAGE_UPLOAD: String = "https://jrrobo.com/FileUpload/UploadImage"

        //Endpoint to get image from server
        const val GET_IMAGE: String ="https://jrrobo.com/uploads/"

        //Endpoint to get all question without paging
        const val GET_ALL_QUESTIONS = ""

        const val APP_OFFER :String ="appOffer"

        // Endpoint to get app course
        const val APP_COURSE: String = "appCourse"

        // Endpoint to get app grade
        const val APP_GRADE: String = "appGrade"

    }

}
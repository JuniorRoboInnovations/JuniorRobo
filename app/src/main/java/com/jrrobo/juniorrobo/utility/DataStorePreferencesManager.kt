package com.jrrobo.juniorrobo.utility

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The data store preferences are arranged as the flow of the application
 */

// preference for on-boarding status of the user
private val Context.onBoardingDataStore by preferencesDataStore("onBoardingStatus")

// preference for whether the user has verified his/her mobile number with OTP
private val Context.otpVerificationDataStore by preferencesDataStore("otpVerification")

// preference to store the mobile number after successfully verification
private val Context.contactNumberWithCountryCode by preferencesDataStore("contactNumberWithCountryCode")

// preference for whether the user has created and updated his profile or not
private val Context.profileCreatedDataStore by preferencesDataStore("profileCreatedStatus")

// preference for the Primary key of the user after OTP verification
private val Context.pkStudentId by preferencesDataStore("pkStudentId")

@Singleton
class DataStorePreferencesManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    // Create the private variables of each of the Data Store preferences
    private val onBoardingStatus = context.onBoardingDataStore
    private val otpVerificationStatus = context.otpVerificationDataStore
    private val contactNumberWithCountryCode = context.contactNumberWithCountryCode
    private val profileCreatedStatus = context.profileCreatedDataStore
    private val pkStudentIdData = context.pkStudentId

    // Companion objects of preference keys
    companion object {
        val onBoardingStatusPreferencesKey = booleanPreferencesKey("ONBOARD_STATUS")
        val otpVerificationStatusPreferencesKey = booleanPreferencesKey("OTP_VERIFICATION")
        val contactNumberPreferenceKey = stringPreferencesKey("CONTACT_NO_WITH_COUNTRY_CODE")
        val profileCreatedStatusPreferenceKey = booleanPreferencesKey("PROFILE_CREATE")
        val pkStudentIdDataPreferenceKey = intPreferencesKey("PK_STUDENT_ID")
    }

    /**
     * Onboard status of the application
     */
    // setter function for the onBoarding status of the application
    suspend fun setOnBoardStatus(onBoardStatusBoolean: Boolean) {
        onBoardingStatus.edit { onBoardStatusMutablePreference ->
            onBoardStatusMutablePreference[onBoardingStatusPreferencesKey] = onBoardStatusBoolean
        }
    }

    // getter function for the onBoarding status of the application returning Flow
    fun getOnBoardingStatus() = onBoardingStatus.data.map { onBoardPreference ->
        onBoardPreference[onBoardingStatusPreferencesKey] ?: false
    }

    /**
     * OTP verifications status
     */
    // setter function for the otpVerification status of the application
    suspend fun setOtpVerificationStatus(otpVerifiedBoolean: Boolean) {
        otpVerificationStatus.edit { otpVerificationStatusMutablePreference ->
            otpVerificationStatusMutablePreference[otpVerificationStatusPreferencesKey] =
                otpVerifiedBoolean
        }
    }

    // getter function for the otpVerification status of the application returning Flow
    fun getOtpVerificationStatus() =
        otpVerificationStatus.data.map { otpVerificationPreference ->
            otpVerificationPreference[otpVerificationStatusPreferencesKey] ?: false
        }

    /**
     * Contact Number with country code
     */
    // setter function for the contact number with country code
    suspend fun setContactNumberWithCountryCode(contactNumberCountryCodeString: String) {
        contactNumberWithCountryCode.edit { contactNumberWithCountryCodeMutablePreference ->
            contactNumberWithCountryCodeMutablePreference[contactNumberPreferenceKey] =
                contactNumberCountryCodeString
        }
    }

    // getter function for the retrieving the contact number to update profile name
    suspend fun getContactNumberWithCountryCode() =
        contactNumberWithCountryCode.data.map { contactNumberWithCountryCodePreference ->
            contactNumberWithCountryCodePreference[contactNumberPreferenceKey] ?: 0
        }

    /**
     * Profile update status of the Student, this is used if the student has not created or updated
     * the profile then the status is held here. If Student has created or updated the profile
     * then we can simply fetch his data from the backend if not then the student is requested
     * to create or update the profile
     */
    // setter function for the profileCreation status of the application
    suspend fun setProfileCreatedStatus(profileCreatedBoolean: Boolean) {
        profileCreatedStatus.edit { profileCreatedStatusMutablePreference ->
            profileCreatedStatusMutablePreference[profileCreatedStatusPreferenceKey] =
                profileCreatedBoolean
        }
    }

    // getter function for the profileCreation status of the application returning Flow
    fun getProfileCreatedStatus() =
        profileCreatedStatus.data.map { profileCreatedStatusPreference ->
            profileCreatedStatusPreference[profileCreatedStatusPreferenceKey] ?: false
        }


    /**
     * Student primary key after OTP verification
     */
    // setter function for the pkStudentId
    suspend fun setPkStudentId(pkStudentId: Int) {
        pkStudentIdData.edit { pkStudentIdMutablePreference ->
            pkStudentIdMutablePreference[pkStudentIdDataPreferenceKey] = pkStudentId
        }
    }

    // getter function for the pkStudentId returning Flow
    fun getPkStudentId() = pkStudentIdData.data.map { pkStudentIdPreference ->
        pkStudentIdPreference[pkStudentIdDataPreferenceKey] ?: -1
    }
}

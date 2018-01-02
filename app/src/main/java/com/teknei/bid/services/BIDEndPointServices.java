package com.teknei.bid.services;

import android.accounts.Account;

import com.teknei.bid.domain.AccountDTO;
import com.teknei.bid.domain.AddressDTO;
import com.teknei.bid.domain.ConfirmFingerSingDTO;
import com.teknei.bid.domain.CredentialDTO;
import com.teknei.bid.domain.FingerLoginDTO;
import com.teknei.bid.domain.FingerSingDTO;
import com.teknei.bid.domain.MailVerificationOTPDTO;
import com.teknei.bid.domain.SearchDTO;
import com.teknei.bid.domain.StartOperationDTO;
import com.teknei.bid.domain.ValidateOtpDTO;
import com.teknei.bid.domain.VerifyCecobanDTO;
import com.teknei.bid.response.OAuthAccessToken;
import com.teknei.bid.response.ResponseDetail;
import com.teknei.bid.response.ResponseDocument;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.response.ResponseStartOpe;
import com.teknei.bid.response.ResponseStep;
import com.teknei.bid.response.ResponseTimeStamp;
import com.teknei.bid.response.ResponseVerifyCecoban;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rgarciav on 25/10/2017.
 */

public interface BIDEndPointServices {

    @GET("rest/v3/enrollment/address/address/find/{id}")
    Call<ResponseDocument> enrollmentAddressFind(@Header("Authorization") String authorization,
                                                 @Path("id") Integer addressId);

    @GET("rest/v3/enrollment/address/findDetail/{id}")
    Call<ResponseServicesBID> enrollmentAddressFindDetail(@Header("Authorization") String authorization,
                                                          @Path("id") Integer addressId);

    @DELETE("rest/v3/enrollment/status/cancel?operationId=idOperation")
    Call<ResponseServicesBID> enrollmentStatusCancelOperation(@Header("Authorization") String authorization,
                                                              @Query("idOperation") String id);

    @POST("rest/v3/enrollment/client/detail/step")
    Call<ResponseStep> enrollmentClientDetailStep (@Header("Authorization") String authorization,
                                                   @Body SearchDTO searchDTO);

    @POST("rest/v3/enrollment/status/start")
    Call<ResponseStartOpe> enrollmentStatusStart (@Header("Authorization") String authorization,
                                                  @Body StartOperationDTO startOperationDTO);

    @POST("rest/v3/enrollment/client/detail/detail")
    Call<ResponseDetail> enrollmentClientDetail (@Header("Authorization") String authorization,
                                                 @Body SearchDTO searchDTO);

    @Multipart
    @POST("rest/v3/enrollment/credentials/credential")
    Call<ResponseServicesBID> enrollmentCredential (@Header("Authorization") String authorization,
                                                    @Part MultipartBody.Part jsonFile,
                                                    @Part MultipartBody.Part imageFileFront,
                                                    @Part MultipartBody.Part imageFileBack);

    @Multipart
    @POST("rest/v3/enrollment/facial/face")
    Call<ResponseServicesBID> enrollmentFacialFace (@Header("Authorization") String authorization,
                                                    @Part MultipartBody.Part jsonFile,
                                                    @Part MultipartBody.Part imageFile);

    @Multipart
    @POST("rest/v3/enrollment/address/comprobanteParsed")
    Call<ResponseDocument> enrollmentAddressComprobanteParsed (@Header("Authorization") String authorization,
                                                               @Part MultipartBody.Part jsonFile,
                                                               @Part MultipartBody.Part imageFile);

    @Multipart
    @POST("rest/v3/enrollment/biometric/minucias")
    Call<ResponseServicesBID> enrollmentBiometricMinucias (@Header("Authorization") String authorization,
                                                           @Part MultipartBody.Part jsonFile);

    @POST("rest/v3/enrollment/status/end/")
    Call<ResponseServicesBID> enrollmentStatusEnd (@Header("Authorization") String authorization,
                                                   @Body String peticion);

    @GET("rest/v3/enrollment/contract/contrato/{id}")
    Call<ResponseBody> enrollmentContract (@Header("Authorization") String authorization,
                                           @Path("id") String id);

    @Multipart
    @POST("rest/v3/enrollment/contract/contrato/add/{id}")
    Call<ResponseServicesBID> enrollmentContractAdd (@Header("Authorization") String authorization,
                                                     @Path("id") String id,
                                                     @Part MultipartBody.Part pdfFile);

    @GET("rest/v3/enrollment/client/detail/search/customer/ts/{operationId}/{curp}")
    Call<ResponseTimeStamp> enrollmentClientDetailSearchCustomerTs
                                                    (@Header("Authorization") String authorization,
                                                     @Path("operationId") String idOpe,
                                                     @Path("curp") String curp);

    @PUT("rest/v3/enrollment/credentials/credential/update/{type}/{id}")
    Call<ResponseServicesBID> enrollmentCredentialUpdate(@Header("Authorization") String authorization,
                                                         @Path("type")   String type,
                                                         @Path("id")     String id,
                                                         @Body CredentialDTO ineDTO);

    @POST("rest/v3/enrollment/address/updateManually/{id}/{type}")
    Call<ResponseServicesBID> enrollmentAddressUpdate(@Header("Authorization") String authorization,
                                                      @Path("id")      String id,
                                                      @Path("type")    String type,
                                                      @Body AddressDTO addressDTO);

    @POST("rest/v3/enrollment/biometric/search/customerId")
    Call<ResponseServicesBID> enrollmentBiometricSearchCustomerId
                                                    (@Header("Authorization") String authorization,
                                                     @Body FingerLoginDTO jsonFile);

    @POST ("rest/v3/enrollment/mail/mail/verification/otp")
    Call<String> enrollmentMailVerificationOTP (@Header("Authorization") String authorization,
                                                @Body MailVerificationOTPDTO validate);

    @POST ("rest/v3/enrollment/mail/validateOTP")
    Call<String> enrollmentMailValidateOTP (@Header("Authorization") String authorization,
                                                         @Body ValidateOtpDTO validate);

    @POST ("rest/v3/enrollment/contract/contrato/sign")
    Call<ResponseServicesBID> enrollmentContractSign (@Header("Authorization") String authorization,
                                                      @Body FingerSingDTO jsonFile);

    @POST ("rest/v3/enrollment/mail/contractSigned")
    Call<ResponseServicesBID> enrollmentMailContractSign (@Header("Authorization") String authorization,
                                                          @Body ConfirmFingerSingDTO jsonFile);

    @GET("rest/v3/enrollment/pictures/search/customer/image/{option}/{curp}/{id}")
    Call<ResponseBody> enrollmentPicturesSearchCustomerImage (@Header("Authorization") String authorization,
                                                              @Path("option") String option,
                                                              @Path("curp") String curp,
                                                              @Path("id") String id);

    @POST("rest/v3/enrollment/credentials/verifyCecoban")
    Call<ResponseVerifyCecoban> enrollmentCredentialsVerifyCecoban (@Header("Authorization") String authorization,
                                                                    @Body VerifyCecobanDTO temp);

    @GET ("rest/v3/management/admin/usua")
    Call<List<AccountDTO>> managementAdminUsuaCheck (@Header("Authorization") String authorization);

    @POST ("rest/v3/management/admin/usua/")
    Call<ResponseServicesBID> managementAdminUsua (@Header("Authorization") String authorization,
                                                     @Body AccountDTO accountDTO);

}

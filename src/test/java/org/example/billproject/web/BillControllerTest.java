package org.example.billproject.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.example.billproject.config.GlobalExceptionHandler;
import org.example.billproject.domain.exception.BillNotFoundException;
import org.example.billproject.domain.model.Bill;
import org.example.billproject.domain.model.BillStatus;
import org.example.billproject.service.BillService;
import org.example.billproject.web.dto.BillPaymentRequest;
import org.example.billproject.web.dto.BillRequest;
import org.example.billproject.web.dto.BillStatusUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(BillController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@Import({GlobalExceptionHandler.class, BillControllerTest.TestConfig.class})
class BillControllerTest {

    private static final String BASE_URL = "/api/bills";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BillService billService;

    private Bill sampleBill;

    @BeforeEach
    void setUp() {
        sampleBill = createBill();
    }

    @Test
    void listShouldReturnBills() throws Exception {
        given(billService.findAll(null)).willReturn(List.of(sampleBill));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleBill.getId().toString()))
                .andDo(document("bills-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("status").optional().description("Optional filter by bill status")),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("Bill identifier"),
                                fieldWithPath("[].customerId").type(JsonFieldType.STRING).description("Customer external identifier"),
                                fieldWithPath("[].customerName").type(JsonFieldType.STRING).description("Customer name"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("Bill description"),
                                fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("Bill amount"),
                                fieldWithPath("[].dueDate").type(JsonFieldType.STRING).description("Due date (ISO-8601)"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("Bill status"),
                                fieldWithPath("[].issuedAt").type(JsonFieldType.STRING).description("Issuing timestamp"),
                                fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("Last update timestamp"),
                                fieldWithPath("[].lastPaymentReference").type(JsonFieldType.STRING)
                                        .description("Reference of the latest payment").optional())));
    }

    @Test
    void getByIdShouldReturnBill() throws Exception {
        given(billService.findById(sampleBill.getId())).willReturn(sampleBill);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", sampleBill.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleBill.getId().toString()))
                .andDo(document("bills-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Bill identifier")),
                        responseFields(billResponseFields())));
    }

    @Test
    void createShouldPersistBill() throws Exception {
        given(billService.create(any(Bill.class))).willReturn(sampleBill);

        BillRequest request = buildRequest();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/bills/" + sampleBill.getId()))
                .andExpect(jsonPath("$.id").value(sampleBill.getId().toString()))
                .andDo(document("bills-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(billRequestFields()),
                        responseFields(billResponseFields())));
    }

    @Test
    void updateShouldReplaceBill() throws Exception {
        given(billService.update(eq(sampleBill.getId()), any(Bill.class))).willReturn(sampleBill);

        BillRequest request = buildRequest();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", sampleBill.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleBill.getId().toString()))
                .andDo(document("bills-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("Bill identifier")),
                        requestFields(billRequestFields()),
                        responseFields(billResponseFields())));
    }

    @Test
    void updateStatusShouldChangeBillStatus() throws Exception {
        given(billService.updateStatus(eq(sampleBill.getId()), eq(BillStatus.CANCELLED))).willReturn(sampleBill);

        BillStatusUpdateRequest request = new BillStatusUpdateRequest();
        request.setStatus(BillStatus.CANCELLED);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/{id}/status", sampleBill.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("bills-update-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("Bill identifier")),
                        requestFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("New bill status")),
                        responseFields(billResponseFields())));
    }

    @Test
    void registerPaymentShouldReturnUpdatedBill() throws Exception {
        given(billService.registerPayment(eq(sampleBill.getId()), eq("PAY-001"))).willReturn(sampleBill);

        BillPaymentRequest request = new BillPaymentRequest();
        request.setPaymentReference("PAY-001");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/{id}/payments", sampleBill.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("bills-register-payment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("Bill identifier")),
                        requestFields(
                                fieldWithPath("paymentReference").type(JsonFieldType.STRING)
                                        .description("Payment reference supplied by the PSP")),
                        responseFields(billResponseFields())));
    }

    @Test
    void deleteShouldRemoveBill() throws Exception {
        doNothing().when(billService).delete(sampleBill.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", sampleBill.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("bills-delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(parameterWithName("id").description("Bill identifier"))));
    }

    @Test
    void shouldReturnNotFoundWhenBillMissing() throws Exception {
        UUID id = UUID.randomUUID();
        given(billService.findById(id)).willThrow(new BillNotFoundException(id));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(document("bills-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("id").description("Bill identifier")),
                        responseFields(errorResponseFields())));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        BillRequest request = new BillRequest();
        request.setCustomerId("");
        request.setCustomerName("");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(document("bills-validation-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(errorResponseFields())));
    }

    private Bill createBill() {
        Bill bill = new Bill();
        bill.setId(UUID.randomUUID());
        bill.setCustomerId("CUST-001");
        bill.setCustomerName("Wayne Enterprises");
        bill.setDescription("Security services");
        bill.setAmount(new BigDecimal("1200.00"));
        bill.setDueDate(LocalDate.now().plusDays(30));
        bill.setStatus(BillStatus.ISSUED);
        bill.setIssuedAt(OffsetDateTime.now().minusDays(1));
        bill.setUpdatedAt(OffsetDateTime.now());
        bill.setLastPaymentReference("PAY-XYZ");
        return bill;
    }

    private BillRequest buildRequest() {
        BillRequest request = new BillRequest();
        request.setCustomerId("CUST-001");
        request.setCustomerName("Wayne Enterprises");
        request.setDescription("Security services");
        request.setAmount(new BigDecimal("1200.00"));
        request.setDueDate(LocalDate.now().plusDays(30));
        request.setStatus(BillStatus.ISSUED);
        return request;
    }

    private FieldDescriptor[] billRequestFields() {
        return new FieldDescriptor[]{
                fieldWithPath("customerId").type(JsonFieldType.STRING).description("Customer external identifier"),
                fieldWithPath("customerName").type(JsonFieldType.STRING).description("Customer name"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("Bill description"),
                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("Bill amount"),
                fieldWithPath("dueDate").type(JsonFieldType.STRING).description("Due date (ISO-8601 date)"),
                fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("Bill status (defaults to ISSUED when omitted)").optional()
        };
    }

    private FieldDescriptor[] billResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("id").type(JsonFieldType.STRING).description("Bill identifier"),
                fieldWithPath("customerId").type(JsonFieldType.STRING).description("Customer external identifier"),
                fieldWithPath("customerName").type(JsonFieldType.STRING).description("Customer name"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("Bill description"),
                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("Bill amount"),
                fieldWithPath("dueDate").type(JsonFieldType.STRING).description("Due date (ISO-8601)"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("Bill status"),
                fieldWithPath("issuedAt").type(JsonFieldType.STRING).description("Issuing timestamp"),
                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("Last update timestamp"),
                fieldWithPath("lastPaymentReference").type(JsonFieldType.STRING)
                        .description("Reference of the latest payment").optional()
        };
    }

    private FieldDescriptor[] errorResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("Time of the error"),
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP status code"),
                fieldWithPath("error").type(JsonFieldType.STRING).description("HTTP reason phrase"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("Human readable message"),
                fieldWithPath("details").type(JsonFieldType.ARRAY).description("Validation details (when available)").optional()
        };
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        BillService billService() {
            return Mockito.mock(BillService.class);
        }
    }
}
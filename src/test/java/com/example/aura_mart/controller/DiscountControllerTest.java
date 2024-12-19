package com.example.aura_mart.controller;

import com.example.aura_mart.dto.DiscountRequestDTO;
import com.example.aura_mart.dto.DiscountResponseDTO;
import com.example.aura_mart.service.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = DiscountController.class,
excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private ObjectMapper objectMapper;

    private DiscountRequestDTO discountRequestDTO;
    private DiscountResponseDTO discountResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup sample DiscountRequestDTO
        discountRequestDTO = DiscountRequestDTO.builder()
                .discountCode("SUMMER2024")
                .discountPercentage(BigDecimal.valueOf(20.00))
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(2))
                .description("Summer Sale Discount")
                .isActive(true)
                .imageUrl("https://example.com/summer-sale.jpg")
                .build();

        // Setup sample DiscountResponseDTO
        discountResponseDTO = DiscountResponseDTO.builder()
                .discountId(1L)
                .discountCode("SUMMER2024")
                .discountPercentage(BigDecimal.valueOf(20.00))
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(2))
                .description("Summer Sale Discount")
                .isActive(true)
                .imageUrl("https://example.com/summer-sale.jpg")
                .build();
    }

    @Test
    void createDiscount() throws Exception {
        when(discountService.createDiscount(any(DiscountRequestDTO.class)))
                .thenReturn(discountResponseDTO);

        mockMvc.perform(post("/api/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.discountCode").value("SUMMER2024"))
                .andExpect(jsonPath("$.discountPercentage").value(20.00));
    }

    @Test
    void updateDiscount() throws Exception {
        when(discountService.updateDiscount(eq(1L), any(DiscountRequestDTO.class)))
                .thenReturn(discountResponseDTO);

        mockMvc.perform(put("/api/discounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountCode").value("SUMMER2024"));
    }

    @Test
    void getDiscountById() throws Exception {
        when(discountService.getDiscountById(1L))
                .thenReturn(discountResponseDTO);

        mockMvc.perform(get("/api/discounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountId").value(1L))
                .andExpect(jsonPath("$.discountCode").value("SUMMER2024"));
    }

    @Test
    void getDiscountByCode() throws Exception {
        when(discountService.getDiscountByCode("SUMMER2024"))
                .thenReturn(discountResponseDTO);

        mockMvc.perform(get("/api/discounts/code/SUMMER2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountCode").value("SUMMER2024"));
    }

    @Test
    void getAllDiscounts() throws Exception {
        List<DiscountResponseDTO> discounts = Arrays.asList(
                discountResponseDTO,
                DiscountResponseDTO.builder()
                        .discountId(2L)
                        .discountCode("WINTER2024")
                        .build()
        );

        when(discountService.getAllDiscounts())
                .thenReturn(discounts);

        mockMvc.perform(get("/api/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].discountCode").value("SUMMER2024"));
    }



    @Test
    void deleteDiscount() throws Exception {
        doNothing().when(discountService).deleteDiscount(1L);

        mockMvc.perform(delete("/api/discounts/1"))
                .andExpect(status().isNoContent());
    }


}
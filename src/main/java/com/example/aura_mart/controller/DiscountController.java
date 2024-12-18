package com.example.aura_mart.controller;

import com.example.aura_mart.dto.DiscountRequestDTO;
import com.example.aura_mart.dto.DiscountResponseDTO;
import com.example.aura_mart.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @Operation(summary = "Create a new discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discount created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<DiscountResponseDTO> createDiscount(@RequestBody DiscountRequestDTO discountRequestDTO) {
        return new ResponseEntity<>(discountService.createDiscount(discountRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Discount not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponseDTO> updateDiscount(@PathVariable("id") Long discountId,
                                                              @RequestBody DiscountRequestDTO discountRequestDTO) {
        return ResponseEntity.ok(discountService.updateDiscount(discountId, discountRequestDTO));
    }

    @Operation(summary = "Get a discount by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Discount not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiscountResponseDTO> getDiscountById(@PathVariable("id") Long discountId) {
        return ResponseEntity.ok(discountService.getDiscountById(discountId));
    }

    @Operation(summary = "Get a discount by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Discount not found",
                    content = @Content)
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<DiscountResponseDTO> getDiscountByCode(@PathVariable("code") String discountCode) {
        return ResponseEntity.ok(discountService.getDiscountByCode(discountCode));
    }

    @Operation(summary = "Get all discounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discounts retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))})
    })
    @GetMapping
    public ResponseEntity<List<DiscountResponseDTO>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @Operation(summary = "Get all active discounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active discounts retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))})
    })
    @GetMapping("/active")
    public ResponseEntity<List<DiscountResponseDTO>> getActiveDiscounts() {
        return ResponseEntity.ok(discountService.getActiveDiscounts());
    }

    @Operation(summary = "Delete a discount by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discount deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable("id") Long discountId) {
        discountService.deleteDiscount(discountId);
        return ResponseEntity.noContent().build();
    }
}


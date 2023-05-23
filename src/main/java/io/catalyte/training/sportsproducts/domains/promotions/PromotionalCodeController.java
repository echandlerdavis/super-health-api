package io.catalyte.training.sportsproducts.domains.promotions;

import io.catalyte.training.sportsproducts.constants.LoggingConstants;
import io.catalyte.training.sportsproducts.constants.Paths;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import java.math.BigDecimal;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code PromotionalCodeController} class is a REST controller that handles HTTP requests
 * related to promotional codes.
 */
@RestController
@RequestMapping(value = Paths.PROMOCODE_PATH)
public class PromotionalCodeController {

  private final PromotionalCodeService promotionalCodeService;
  private final Logger logger = LogManager.getLogger(PromotionalCodeController.class);

  /**
   * Creates a new {@code PromotionalCodeController} instance with the specified
   * {@code PromotionalCodeService} instance.
   *
   * @param promotionalCodeService the {@code PromotionalCodeService} instance to use
   */
  @Autowired
  public PromotionalCodeController(PromotionalCodeService promotionalCodeService) {
    this.promotionalCodeService = promotionalCodeService;
  }

  /**
   * Handles HTTP POST requests to create a new promotional code with the specified data. The
   * request body must contain a valid JSON representation of the promotional code DTO. If the
   * promotional code is created successfully, a 201 CREATED response with the persisted object is
   * returned. Otherwise, an error response with a 400 BAD REQUEST status code is returned.
   *
   * @param dto the DTO containing the promotional code data to create
   * @return a {@code ResponseEntity} containing the created promotional code and the HTTP status
   * code
   * @throws IllegalArgumentException if the promotional code already exists
   */
  @PostMapping
  public ResponseEntity<PromotionalCode> createPromotionalCode(
      @Valid @RequestBody PromotionalCodeDTO dto) {
    if (dto.getType() == null || dto.getRate() == null) {
      throw new BadRequest("Type and rate must not be null");
    }
    if (dto.getType() == PromotionalCodeType.FLAT
        && dto.getRate().compareTo(BigDecimal.ZERO) == 0) {
      throw new BadRequest("Flat rate must be greater than zero");
    }
    if (dto.getType() == PromotionalCodeType.PERCENT && (
        dto.getRate().compareTo(BigDecimal.ZERO) <= 0
            || dto.getRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
      throw new BadRequest("Percent rate must be between 0 and 100");
    }
    PromotionalCode promotionalCode = promotionalCodeService.createPromotionalCode(dto);
    return new ResponseEntity<>(promotionalCode, HttpStatus.CREATED);
  }

  /**
   * Endpoint to verify that the user provided promotional code exists
   *
   * @param title Title of the desired code
   * @return String - will be an error notification it the code is not valid, or "" if it is valid
   */
  @GetMapping(value = "/{title}")
  public ResponseEntity<PromotionalCode> getByTitle(@PathVariable String title) {
    logger.info(String.format(LoggingConstants.GET_PROMOCODE_FORMAT, title));
    return new ResponseEntity<>(promotionalCodeService.getPromotionalCodeByTitle(title),
        HttpStatus.OK);
  }

//    public static class DuplicatePromoCodeException extends Exception {
//    }
}

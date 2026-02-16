package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.CouponAlreadyDeletedException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDescriptionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private final UUID id;
    private final CouponCode code;
    private final String description;
    private final DiscountValue discountValue;
    private final ExpirationDate expirationDate;
    private final boolean published;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private final LocalDateTime createdAt;

    private Coupon(UUID id, CouponCode code, String description, DiscountValue discountValue,
                   ExpirationDate expirationDate, boolean published, boolean deleted,
                   LocalDateTime deletedAt, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public static Coupon create(String code, String description, BigDecimal discountValue,
                                LocalDate expirationDate, boolean published) {
        validateDescription(description);

        return new Coupon(
                UUID.randomUUID(),
                CouponCode.of(code),
                description.trim(),
                DiscountValue.of(discountValue),
                ExpirationDate.of(expirationDate),
                published,
                false,
                null,
                LocalDateTime.now()
        );
    }

    public static Coupon reconstitute(UUID id, CouponCode code, String description,
                                      DiscountValue discountValue, ExpirationDate expirationDate,
                                      boolean published, boolean deleted,
                                      LocalDateTime deletedAt, LocalDateTime createdAt) {
        return new Coupon(id, code, description, discountValue, expirationDate,
                published, deleted, deletedAt, createdAt);
    }

    public void delete() {
        if (this.deleted) {
            throw new CouponAlreadyDeletedException(
                    "O cupom '" + this.code.value() + "' já foi excluído"
            );
        }
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expirationDate.isExpired();
    }

    public boolean isActive() {
        return !deleted && !isExpired();
    }

    public UUID getId() {
        return id;
    }

    public CouponCode getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public DiscountValue getDiscountValue() {
        return discountValue;
    }

    public ExpirationDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coupon that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Coupon{id=" + id + ", code=" + code + ", deleted=" + deleted + "}";
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidDescriptionException("A descrição é obrigatória");
        }
    }
}

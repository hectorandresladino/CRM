package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "metas_comerciales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaComercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "vendedor")
    private String vendedor;

    @Column(name = "equipo")
    private String equipo;

    @Column(nullable = false)
    private String periodo;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "trimestre")
    private Integer trimestre;

    @Column(name = "mes")
    private Integer mes;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoObjetivo;

    @Column(name = "monto_alcanzado", precision = 15, scale = 2)
    private BigDecimal montoAlcanzado;

    @Column(name = "numero_ventas_objetivo")
    private Integer numeroVentasObjetivo;

    @Column(name = "numero_ventas_real")
    private Integer numeroVentasReal;

    @Column(name = "porcentaje_cumplimiento", precision = 5, scale = 2)
    private BigDecimal porcentajeCumplimiento;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Periodo { MENSUAL, TRIMESTRAL, ANUAL }
}

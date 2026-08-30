package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_configuraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "color_primario")
    private String colorPrimario;

    @Column(name = "color_secundario")
    private String colorSecundario;

    @Column(name = "dominio_personalizado")
    private String dominioPersonalizado;

    @Column(name = "zona_horaria")
    private String zonaHoraria = "America/Bogota";

    @Column(name = "formato_fecha")
    private String formatoFecha = "DD/MM/YYYY";

    @Column(name = "formato_moneda")
    private String formatoMoneda = "es-CO";

    @Column(name = "moneda_base")
    private String monedaBase = "COP";

    @Column(name = "idioma_default")
    private String idiomaDefault = "es";

    @Column(name = "prefijo_facturacion")
    private String prefijoFacturacion;

    @Column(name = "consecutivo_factura")
    private Integer consecutivoFactura = 1;

    @Column(name = "prefijo_cotizacion")
    private String prefijoCotizacion;

    @Column(name = "consecutivo_cotizacion")
    private Integer consecutivoCotizacion = 1;

    @Column(name = "prefijo_pedido")
    private String prefijoPedido;

    @Column(name = "consecutivo_pedido")
    private Integer consecutivoPedido = 1;

    @Column(name = "resolucion_facturacion")
    private String resolucionFacturacion;

    @Column(name = "nit_empresa")
    private String nitEmpresa;

    @Column(name = "direccion_empresa")
    private String direccionEmpresa;

    @Column(name = "telefono_empresa")
    private String telefonoEmpresa;

    @Column(name = "email_empresa")
    private String emailEmpresa;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

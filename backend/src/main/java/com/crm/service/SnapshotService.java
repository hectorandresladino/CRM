/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SnapshotService {

    private final TenantRepository tenantRepository;
    private final TenantConfiguracionRepository tenantConfigRepo;
    private final CampoPersonalizadoRepository campoPersonalizadoRepo;
    private final EmailTemplateRepository emailTemplateRepo;
    private final FormularioWebRepository formularioWebRepo;
    private final ReglaAutomaticaRepository reglaAutomaticaRepo;

    public Map<String, Object> createSnapshot(Long sourceTenantId) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("sourceTenantId", sourceTenantId);
        snapshot.put("createdAt", java.time.LocalDateTime.now().toString());

        List<Map<String, Object>> customFields = new ArrayList<>();
        for (CampoPersonalizado cf : campoPersonalizadoRepo.findByTenantId(sourceTenantId)) {
            Map<String, Object> field = new LinkedHashMap<>();
            field.put("entidad", cf.getEntidad());
            field.put("nombreCampo", cf.getNombreCampo());
            field.put("tipoCampo", cf.getTipoCampo());
            field.put("etiqueta", cf.getEtiqueta());
            field.put("requerido", cf.getRequerido());
            field.put("opciones", cf.getOpciones());
            customFields.add(field);
        }
        snapshot.put("customFields", customFields);

        List<Map<String, Object>> emailTemplates = new ArrayList<>();
        for (EmailTemplate et : emailTemplateRepo.findByTenantId(sourceTenantId)) {
            Map<String, Object> tpl = new LinkedHashMap<>();
            tpl.put("nombre", et.getNombre());
            tpl.put("asunto", et.getAsunto());
            tpl.put("cuerpo", et.getCuerpo());
            tpl.put("idioma", et.getIdioma());
            emailTemplates.add(tpl);
        }
        snapshot.put("emailTemplates", emailTemplates);

        List<Map<String, Object>> forms = new ArrayList<>();
        for (FormularioWeb fw : formularioWebRepo.findByTenantId(sourceTenantId)) {
            Map<String, Object> form = new LinkedHashMap<>();
            form.put("nombre", fw.getNombre());
            form.put("descripcion", fw.getDescripcion());
            form.put("campos", fw.getCampos());
            form.put("entidadDestino", fw.getEntidadDestino());
            forms.add(form);
        }
        snapshot.put("forms", forms);

        List<Map<String, Object>> automations = new ArrayList<>();
        for (ReglaAutomatica ra : reglaAutomaticaRepo.findByTenantId(sourceTenantId)) {
            Map<String, Object> auto = new LinkedHashMap<>();
            auto.put("nombre", ra.getNombre());
            auto.put("trigger", ra.getTrigger());
            auto.put("condiciones", ra.getCondiciones());
            auto.put("acciones", ra.getAcciones());
            auto.put("activa", ra.getActiva());
            automations.add(auto);
        }
        snapshot.put("automations", automations);

        log.info("Snapshot created for tenant {} with {} fields, {} templates, {} forms, {} automations",
                sourceTenantId, customFields.size(), emailTemplates.size(), forms.size(), automations.size());
        return snapshot;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> applySnapshot(Long targetTenantId, Map<String, Object> snapshot) {
        int applied = 0;

        List<Map<String, Object>> customFields = (List<Map<String, Object>>) snapshot.get("customFields");
        if (customFields != null) {
            for (Map<String, Object> field : customFields) {
                CampoPersonalizado cf = new CampoPersonalizado();
                cf.setTenantId(targetTenantId);
                cf.setEntidad((String) field.get("entidad"));
                cf.setNombreCampo((String) field.get("nombreCampo"));
                cf.setTipoCampo((String) field.get("tipoCampo"));
                cf.setEtiqueta((String) field.get("etiqueta"));
                cf.setRequerido((Boolean) field.get("requerido"));
                cf.setOpciones((String) field.get("opciones"));
                campoPersonalizadoRepo.save(cf);
                applied++;
            }
        }

        List<Map<String, Object>> emailTemplates = (List<Map<String, Object>>) snapshot.get("emailTemplates");
        if (emailTemplates != null) {
            for (Map<String, Object> tpl : emailTemplates) {
                EmailTemplate et = new EmailTemplate();
                et.setTenantId(targetTenantId);
                et.setNombre((String) tpl.get("nombre"));
                et.setAsunto((String) tpl.get("asunto"));
                et.setCuerpo((String) tpl.get("cuerpo"));
                et.setIdioma((String) tpl.get("idioma"));
                emailTemplateRepo.save(et);
                applied++;
            }
        }

        List<Map<String, Object>> forms = (List<Map<String, Object>>) snapshot.get("forms");
        if (forms != null) {
            for (Map<String, Object> form : forms) {
                FormularioWeb fw = new FormularioWeb();
                fw.setTenantId(targetTenantId);
                fw.setNombre((String) form.get("nombre"));
                fw.setDescripcion((String) form.get("descripcion"));
                fw.setCampos((String) form.get("campos"));
                fw.setEntidadDestino((String) form.get("entidadDestino"));
                formularioWebRepo.save(fw);
                applied++;
            }
        }

        List<Map<String, Object>> automations = (List<Map<String, Object>>) snapshot.get("automations");
        if (automations != null) {
            for (Map<String, Object> auto : automations) {
                ReglaAutomatica ra = new ReglaAutomatica();
                ra.setTenantId(targetTenantId);
                ra.setNombre((String) auto.get("nombre"));
                ra.setTrigger((String) auto.get("trigger"));
                ra.setCondiciones((String) auto.get("condiciones"));
                ra.setAcciones((String) auto.get("acciones"));
                ra.setActiva((Boolean) auto.get("activa"));
                reglaAutomaticaRepo.save(ra);
                applied++;
            }
        }

        log.info("Snapshot applied to tenant {}: {} items", targetTenantId, applied);
        return Map.of("targetTenantId", targetTenantId, "itemsApplied", applied);
    }
}

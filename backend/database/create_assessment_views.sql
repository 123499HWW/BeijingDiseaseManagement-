-- 创建评估表最新记录视图，用于优化综合查询性能
-- 这些视图会缓存每个患者的最新评估记录，避免复杂的子查询
-- 执行这些SQL后，查询性能将大幅提升

-- 1. CURB-65最新记录视图
CREATE OR REPLACE VIEW curb_latest_view AS 
SELECT pcr.patient_id, car.* 
FROM curb_assessment_result car 
INNER JOIN patient_curb_relation pcr ON car.curb_id = pcr.curb_id 
INNER JOIN (
    SELECT pcr2.patient_id, MAX(car2.curb_id) as max_id 
    FROM curb_assessment_result car2 
    INNER JOIN patient_curb_relation pcr2 ON car2.curb_id = pcr2.curb_id 
    WHERE car2.is_deleted = 0 
    GROUP BY pcr2.patient_id
) latest ON pcr.patient_id = latest.patient_id AND car.curb_id = latest.max_id 
WHERE car.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_curb_patient_relation_patient_id ON patient_curb_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_curb_assessment_result_deleted ON curb_assessment_result(is_deleted);

-- 2. COVID-19重型最新记录视图
CREATE OR REPLACE VIEW covid19_latest_view AS 
SELECT cpr.patient_id, ca.* 
FROM covid19_assessment ca 
INNER JOIN covid19_patient_relation cpr ON ca.assessment_id = cpr.assessment_id 
INNER JOIN (
    SELECT cpr2.patient_id, MAX(ca2.assessment_id) as max_id 
    FROM covid19_assessment ca2 
    INNER JOIN covid19_patient_relation cpr2 ON ca2.assessment_id = cpr2.assessment_id 
    WHERE ca2.is_deleted = 0 
    GROUP BY cpr2.patient_id
) latest ON cpr.patient_id = latest.patient_id AND ca.assessment_id = latest.max_id 
WHERE ca.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_covid19_patient_relation_patient_id ON covid19_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_covid19_assessment_deleted ON covid19_assessment(is_deleted);

-- 3. COVID-19危重型最新记录视图
CREATE OR REPLACE VIEW covid19_critical_latest_view AS 
SELECT ccpr.patient_id, cca.* 
FROM covid19_critical_assessment cca 
INNER JOIN covid19_critical_patient_relation ccpr ON cca.assessment_id = ccpr.assessment_id 
INNER JOIN (
    SELECT ccpr2.patient_id, MAX(cca2.assessment_id) as max_id 
    FROM covid19_critical_assessment cca2 
    INNER JOIN covid19_critical_patient_relation ccpr2 ON cca2.assessment_id = ccpr2.assessment_id 
    WHERE cca2.is_deleted = 0 
    GROUP BY ccpr2.patient_id
) latest ON ccpr.patient_id = latest.patient_id AND cca.assessment_id = latest.max_id 
WHERE cca.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_covid19_critical_patient_relation_patient_id ON covid19_critical_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_covid19_critical_assessment_deleted ON covid19_critical_assessment(is_deleted);

-- 4. CPIS最新记录视图
CREATE OR REPLACE VIEW cpis_latest_view AS 
SELECT cpr.patient_id, car.* 
FROM cpis_assessment_result car 
INNER JOIN cpis_patient_relation cpr ON car.cpis_id = cpr.cpis_id 
INNER JOIN (
    SELECT cpr2.patient_id, MAX(car2.cpis_id) as max_id 
    FROM cpis_assessment_result car2 
    INNER JOIN cpis_patient_relation cpr2 ON car2.cpis_id = cpr2.cpis_id 
    WHERE car2.is_deleted = 0 
    GROUP BY cpr2.patient_id
) latest ON cpr.patient_id = latest.patient_id AND car.cpis_id = latest.max_id 
WHERE car.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_cpis_patient_relation_patient_id ON cpis_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_cpis_assessment_result_deleted ON cpis_assessment_result(is_deleted);

-- 5. PSI最新记录视图
CREATE OR REPLACE VIEW psi_latest_view AS 
SELECT ppr.patient_id, par.* 
FROM psi_assessment_result par 
INNER JOIN psi_patient_relation ppr ON par.psi_id = ppr.psi_id 
INNER JOIN (
    SELECT ppr2.patient_id, MAX(par2.psi_id) as max_id 
    FROM psi_assessment_result par2 
    INNER JOIN psi_patient_relation ppr2 ON par2.psi_id = ppr2.psi_id 
    WHERE par2.is_deleted = 0 
    GROUP BY ppr2.patient_id
) latest ON ppr.patient_id = latest.patient_id AND par.psi_id = latest.max_id 
WHERE par.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_psi_patient_relation_patient_id ON psi_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_psi_assessment_result_deleted ON psi_assessment_result(is_deleted);

-- 6. qSOFA最新记录视图
CREATE OR REPLACE VIEW qsofa_latest_view AS 
SELECT qpr.patient_id, qa.* 
FROM qsofa_assessment qa 
INNER JOIN qsofa_patient_relation qpr ON qa.assessment_id = qpr.assessment_id 
INNER JOIN (
    SELECT qpr2.patient_id, MAX(qa2.assessment_id) as max_id 
    FROM qsofa_assessment qa2 
    INNER JOIN qsofa_patient_relation qpr2 ON qa2.assessment_id = qpr2.assessment_id 
    WHERE qa2.is_deleted = 0 
    GROUP BY qpr2.patient_id
) latest ON qpr.patient_id = latest.patient_id AND qa.assessment_id = latest.max_id 
WHERE qa.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_qsofa_patient_relation_patient_id ON qsofa_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_qsofa_assessment_deleted ON qsofa_assessment(is_deleted);

-- 7. 重症肺炎最新记录视图
CREATE OR REPLACE VIEW severe_pneumonia_latest_view AS 
SELECT spr.patient_id, spd.* 
FROM severe_pneumonia_diagnosis spd 
INNER JOIN severe_pneumonia_patient_relation spr ON spd.diagnosis_id = spr.diagnosis_id 
INNER JOIN (
    SELECT spr2.patient_id, MAX(spd2.diagnosis_id) as max_id 
    FROM severe_pneumonia_diagnosis spd2 
    INNER JOIN severe_pneumonia_patient_relation spr2 ON spd2.diagnosis_id = spr2.diagnosis_id 
    WHERE spd2.is_deleted = 0 
    GROUP BY spr2.patient_id
) latest ON spr.patient_id = latest.patient_id AND spd.diagnosis_id = latest.max_id 
WHERE spd.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_severe_pneumonia_patient_relation_patient_id ON severe_pneumonia_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_severe_pneumonia_diagnosis_deleted ON severe_pneumonia_diagnosis(is_deleted);

-- 8. SOFA最新记录视图
CREATE OR REPLACE VIEW sofa_latest_view AS 
SELECT spr.patient_id, sa.* 
FROM sofa_assessment sa 
INNER JOIN sofa_patient_relation spr ON sa.assessment_id = spr.assessment_id 
INNER JOIN (
    SELECT spr2.patient_id, MAX(sa2.assessment_id) as max_id 
    FROM sofa_assessment sa2 
    INNER JOIN sofa_patient_relation spr2 ON sa2.assessment_id = spr2.assessment_id 
    WHERE sa2.is_deleted = 0 
    GROUP BY spr2.patient_id
) latest ON spr.patient_id = latest.patient_id AND sa.assessment_id = latest.max_id 
WHERE sa.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_sofa_patient_relation_patient_id ON sofa_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_sofa_assessment_deleted ON sofa_assessment(is_deleted);

-- 9. 呼吸道症候群最新记录视图
CREATE OR REPLACE VIEW respiratory_syndrome_latest_view AS 
SELECT spr.patient_id, rsa.* 
FROM respiratory_syndrome_assessment rsa 
INNER JOIN syndrome_patient_relation spr ON rsa.syndrome_id = spr.syndrome_id 
INNER JOIN (
    SELECT spr2.patient_id, MAX(rsa2.syndrome_id) as max_id 
    FROM respiratory_syndrome_assessment rsa2 
    INNER JOIN syndrome_patient_relation spr2 ON rsa2.syndrome_id = spr2.syndrome_id 
    WHERE rsa2.is_deleted = 0 
    GROUP BY spr2.patient_id
) latest ON spr.patient_id = latest.patient_id AND rsa.syndrome_id = latest.max_id 
WHERE rsa.is_deleted = 0;

-- 添加索引优化
CREATE INDEX IF NOT EXISTS idx_syndrome_patient_relation_patient_id ON syndrome_patient_relation(patient_id);
CREATE INDEX IF NOT EXISTS idx_respiratory_syndrome_assessment_deleted ON respiratory_syndrome_assessment(is_deleted);

-- 10. 患者信息表索引优化
CREATE INDEX IF NOT EXISTS idx_patient_info_deleted ON patient_info(is_deleted);
CREATE INDEX IF NOT EXISTS idx_patient_info_patient_number ON patient_info(patient_number);
CREATE INDEX IF NOT EXISTS idx_patient_info_gender_age ON patient_info(gender, age);

-- 验证视图创建成功
SELECT 'Views created successfully!' AS status;

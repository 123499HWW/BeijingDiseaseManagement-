-- 综合查询性能优化索引
-- 为了确保查询在2秒内完成，需要创建以下索引

-- ========== 患者表索引 ==========
-- 患者编号索引（支持模糊查询）
CREATE INDEX idx_patient_number ON patient_info(patient_number);
-- 性别和年龄复合索引
CREATE INDEX idx_gender_age ON patient_info(gender, age);
-- 删除标记索引
CREATE INDEX idx_patient_deleted ON patient_info(is_deleted);

-- ========== CURB-65评分表索引 ==========
-- 主键已有索引
-- 总分和风险等级复合索引
CREATE INDEX idx_curb_score_risk ON curb_assessment_result(total_score, risk_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_curb_deleted_created ON curb_assessment_result(is_deleted, created_at DESC);

-- CURB-65关联表索引
CREATE INDEX idx_pcr_patient_id ON patient_curb_relation(patient_id);
CREATE INDEX idx_pcr_curb_id ON patient_curb_relation(curb_id);

-- ========== COVID-19重型诊断表索引 ==========
-- 诊断结果和严重程度复合索引
CREATE INDEX idx_covid19_severe_level ON covid19_assessment(is_severe_type, severity_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_covid19_deleted_created ON covid19_assessment(is_deleted, created_at DESC);

-- COVID-19重型关联表索引
CREATE INDEX idx_cpr_patient_id ON covid19_patient_relation(patient_id);
CREATE INDEX idx_cpr_assessment_id ON covid19_patient_relation(assessment_id);

-- ========== COVID-19危重型诊断表索引 ==========
-- 诊断结果和严重程度复合索引
CREATE INDEX idx_covid19c_critical_level ON covid19_critical_assessment(is_critical_type, severity_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_covid19c_deleted_created ON covid19_critical_assessment(is_deleted, created_at DESC);

-- COVID-19危重型关联表索引
CREATE INDEX idx_ccpr_patient_id ON covid19_critical_patient_relation(patient_id);
CREATE INDEX idx_ccpr_assessment_id ON covid19_critical_patient_relation(assessment_id);

-- ========== CPIS评分表索引 ==========
-- 总分和风险等级复合索引
CREATE INDEX idx_cpis_score_risk ON cpis_assessment_result(total_score, risk_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_cpis_deleted_created ON cpis_assessment_result(is_deleted, created_at DESC);

-- CPIS关联表索引
CREATE INDEX idx_cpisr_patient_id ON cpis_patient_relation(patient_id);
CREATE INDEX idx_cpisr_cpis_id ON cpis_patient_relation(cpis_id);

-- ========== PSI评分表索引 ==========
-- 总分和风险等级复合索引
CREATE INDEX idx_psi_score_class ON psi_assessment_result(total_score, risk_class);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_psi_deleted_created ON psi_assessment_result(is_deleted, created_at DESC);

-- PSI关联表索引
CREATE INDEX idx_ppr_patient_id ON psi_patient_relation(patient_id);
CREATE INDEX idx_ppr_psi_id ON psi_patient_relation(psi_id);

-- ========== qSOFA评分表索引 ==========
-- 总分和风险等级复合索引
CREATE INDEX idx_qsofa_score_risk ON qsofa_assessment(total_score, risk_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_qsofa_deleted_created ON qsofa_assessment(is_deleted, created_at DESC);

-- qSOFA关联表索引
CREATE INDEX idx_qpr_patient_id ON qsofa_patient_relation(patient_id);
CREATE INDEX idx_qpr_assessment_id ON qsofa_patient_relation(assessment_id);

-- ========== 重症肺炎诊断表索引 ==========
-- 诊断结果索引
CREATE INDEX idx_spd_is_severe ON severe_pneumonia_diagnosis(is_severe_pneumonia);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_spd_deleted_created ON severe_pneumonia_diagnosis(is_deleted, created_at DESC);

-- 重症肺炎关联表索引
CREATE INDEX idx_spr_patient_id ON severe_pneumonia_patient_relation(patient_id);
CREATE INDEX idx_spr_diagnosis_id ON severe_pneumonia_patient_relation(diagnosis_id);

-- ========== SOFA评分表索引 ==========
-- 总分和严重程度复合索引
CREATE INDEX idx_sofa_score_level ON sofa_assessment(total_score, severity_level);
-- 删除标记和创建时间复合索引
CREATE INDEX idx_sofa_deleted_created ON sofa_assessment(is_deleted, created_at DESC);

-- SOFA关联表索引
CREATE INDEX idx_sofar_patient_id ON sofa_patient_relation(patient_id);
CREATE INDEX idx_sofar_assessment_id ON sofa_patient_relation(assessment_id);

-- ========== 查询执行计划分析 ==========
-- 使用EXPLAIN分析查询性能
-- EXPLAIN ANALYZE
-- SELECT ... (综合查询SQL);

-- ========== 统计信息更新 ==========
-- 更新表统计信息以优化查询计划
ANALYZE TABLE patient_info;
ANALYZE TABLE curb_assessment_result;
ANALYZE TABLE patient_curb_relation;
ANALYZE TABLE covid19_assessment;
ANALYZE TABLE covid19_patient_relation;
ANALYZE TABLE covid19_critical_assessment;
ANALYZE TABLE covid19_critical_patient_relation;
ANALYZE TABLE cpis_assessment_result;
ANALYZE TABLE cpis_patient_relation;
ANALYZE TABLE psi_assessment_result;
ANALYZE TABLE psi_patient_relation;
ANALYZE TABLE qsofa_assessment;
ANALYZE TABLE qsofa_patient_relation;
ANALYZE TABLE severe_pneumonia_diagnosis;
ANALYZE TABLE severe_pneumonia_patient_relation;
ANALYZE TABLE sofa_assessment;
ANALYZE TABLE sofa_patient_relation;

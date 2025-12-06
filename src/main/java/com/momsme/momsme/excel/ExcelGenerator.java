package com.momsme.momsme.excel;

import com.momsme.momsme.model.MsmeUnitDetails;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelGenerator {
    public byte[] generateExcel(List<MsmeUnitDetails> content) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("MSME Details");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("slno");
            header.createCell(1).setCellValue("uniqueno");
            header.createCell(2).setCellValue("departmentname");
            header.createCell(3).setCellValue("msmestate");
            header.createCell(4).setCellValue("msmesdist");
            header.createCell(5).setCellValue("msmessector");
            header.createCell(6).setCellValue("unitname");
            header.createCell(7).setCellValue("category");
            header.createCell(8).setCellValue("unitaddress");
            header.createCell(9).setCellValue("doorno");
            header.createCell(10).setCellValue("locality");
            header.createCell(11).setCellValue("street");
            header.createCell(12).setCellValue("villageId");
            header.createCell(13).setCellValue("village");
            header.createCell(14).setCellValue("ward");
            header.createCell(15).setCellValue("mandal");
            header.createCell(16).setCellValue("district");
            header.createCell(17).setCellValue("pincode");
            header.createCell(18).setCellValue("officeemail");
            header.createCell(19).setCellValue("officecontact");
            header.createCell(20).setCellValue("principalbusinessplace");
            header.createCell(21).setCellValue("femaleempstotal");
            header.createCell(22).setCellValue("maleempstotal");
            header.createCell(23).setCellValue("lattitude");
            header.createCell(24).setCellValue("longitute");
            header.createCell(25).setCellValue("institutiondetails");


            for(int i=1 ; i<content.size(); i++) {
                // Example row
                Row row = sheet.createRow(i);
                MsmeUnitDetails msmeUnitDetails = content.get(i);
                row.createCell(0).setCellValue(msmeUnitDetails.getSlno());
                row.createCell(1).setCellValue(msmeUnitDetails.getUniqueNo());
                row.createCell(2).setCellValue(msmeUnitDetails.getDepartmentName());
                row.createCell(3).setCellValue(msmeUnitDetails.getMsmeState());
                row.createCell(4).setCellValue(msmeUnitDetails.getMsmeDist());
                row.createCell(5).setCellValue(msmeUnitDetails.getMsmeSector());
                row.createCell(6).setCellValue(msmeUnitDetails.getUnitName());
                row.createCell(7).setCellValue(msmeUnitDetails.getCategory());
                row.createCell(8).setCellValue(msmeUnitDetails.getUnitAddress());
                row.createCell(9).setCellValue(msmeUnitDetails.getDoorNo());
                row.createCell(10).setCellValue(msmeUnitDetails.getLocality());
                row.createCell(11).setCellValue(msmeUnitDetails.getStreet());
                row.createCell(12).setCellValue(msmeUnitDetails.getVillageId());
                row.createCell(13).setCellValue(msmeUnitDetails.getVillage());
                row.createCell(14).setCellValue(msmeUnitDetails.getWard());
                row.createCell(15).setCellValue(msmeUnitDetails.getMandal());
                row.createCell(16).setCellValue(msmeUnitDetails.getDistrict());
                row.createCell(17).setCellValue(msmeUnitDetails.getPinCode());
                row.createCell(18).setCellValue(msmeUnitDetails.getOfficeEmail());
                row.createCell(19).setCellValue(msmeUnitDetails.getOfficeContact());
                row.createCell(20).setCellValue(msmeUnitDetails.getPrincipalBusinessPlace());
                row.createCell(21).setCellValue(msmeUnitDetails.getFemaleEmpsTotal());
                row.createCell(22).setCellValue(msmeUnitDetails.getLatitude());
                row.createCell(23).setCellValue(msmeUnitDetails.getLongitude());
                row.createCell(24).setCellValue(msmeUnitDetails.getInstitutionDetails());
                row.createCell(25).setCellValue(msmeUnitDetails.getPurpose());
                row.createCell(26).setCellValue(msmeUnitDetails.getOrgnType());
            }
            // Auto adjust columns
            for (int i = 0; content.size() < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Return file as byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}

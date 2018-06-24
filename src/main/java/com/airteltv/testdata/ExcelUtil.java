package com.airteltv.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	// public static Map<String, Inner> domMap;

	public static Map<String, Inner> testDataMap;
	int rownum, cellnum;
	File file = null;

	public ExcelUtil() throws IOException {
		if (testDataMap == null)
			readTestDataFile();
	}

	private synchronized void readTestDataFile() throws IOException {
		testDataMap = new ConcurrentHashMap<>();
		String fileName = "TestData.xlsx";
		String filepath = System.getProperty("user.dir") + "/test-data/" + fileName;
		File file = new File(filepath);

		FileInputStream fis = new FileInputStream(file);

		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
			Inner inner = new Inner();
			XSSFSheet sheet = workbook.getSheet(workbook.getSheetName(sheetIndex));
			inner.setMainlist(customizedData(sheet));
			columnDictionary(inner);
			rowDictionary(inner);
			testDataMap.put(workbook.getSheetName(sheetIndex), inner);
		}
	}

	public synchronized List<List<String>> customizedData(XSSFSheet sheet) {

		Inner inner = new Inner();

		rownum = sheet.getLastRowNum() + 1;

		cellnum = sheet.getRow(0).getLastCellNum();

		for (int i = 0; i < rownum; i++) {
			String CellValue = null;
			XSSFRow row = sheet.getRow(i);

			List<String> list = new ArrayList<String>();

			for (int j = 0; j < cellnum; j++) {
				XSSFCell cell = row.getCell(j);

				CellValue = getCellValueAsString(cell);
				list.add(CellValue);
			}
			inner.getMainlist().add(list);
			list = null;
		}
		return inner.getMainlist();
	}

	/**
	 * This method is used to get cell value for types: String, Numeric, Boolean,
	 * Blank.
	 * 
	 */
	public synchronized String getCellValueAsString(XSSFCell cell) {
		String strCellValue = null;
		if (cell == null) {
			strCellValue = "";
		} else {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				strCellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					strCellValue = dateFormat.format(cell.getDateCellValue());
				} else {
					Double value = cell.getNumericCellValue();
					Long longValue = value.longValue();
					strCellValue = new String(longValue.toString());
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				strCellValue = new String(new Boolean(cell.getBooleanCellValue()).toString());
				break;
			case Cell.CELL_TYPE_BLANK:
				strCellValue = "";
				break;
			}
		}

		return strCellValue;
	}

	public synchronized String readCell(Inner inner, int row, int col) {
		// System.out.println(mainlist.get(row).get(col));
		return inner.getMainlist().get(row).get(col);

	}

	public synchronized void rowDictionary(Inner inner) {
		for (int i = 1; i < inner.getMainlist().size(); i++)
			inner.getDictRow().put(readCell(inner, i, 0), Integer.valueOf(i));
	}

	public synchronized int getRow(Inner inner, String rowname) {
		System.out.println(inner.getDictRow());
		System.out.println(inner.getDictRow().get("ALTBALAJI_EPISODE"));
		System.out.println(rowname);
		return inner.getDictRow().get(rowname).intValue();
	}

	public synchronized void columnDictionary(Inner inner) {

		for (int i = 0; i < cellnum; i++) {
			inner.getDictCol().put(readCell(inner, 0, i), Integer.valueOf(i));
		}
	}

	public synchronized int getCol(Inner inner, String colname) {
		return inner.getDictCol().get(colname).intValue();
	}

	/**
	 * This method is used to get test data from excel sheets for the instance.
	 */
	public synchronized String getTestData(final String sheetName, final String rowName, String colName)
			throws IOException {

		final String testStatus = readCell(testDataMap.get(sheetName), getRow(testDataMap.get(sheetName), rowName),
				getCol(testDataMap.get(sheetName), colName));

		return testStatus;
	}

	public synchronized int getCount(Inner inner) {
		return inner.getMainlist().size();

	}

	/*
	 * public int getTestDataRowCount(String sheetName) { int cellCount =
	 * getCount(testDataMap.get(sheetName)); return cellCount-1; }
	 */

	public synchronized String[][] readDataFromExcel(String sheetName, String tabName) throws IOException {
		File excelFile = new File(System.getProperty("user.dir") + "/test-data/" + sheetName + ".xlsx");
		FileInputStream fis = new FileInputStream(excelFile);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		try {
			XSSFSheet sheet = wb.getSheet(tabName);
			int rowNum = sheet.getLastRowNum();
			int colNum = sheet.getRow(0).getLastCellNum();
			String data[][] = new String[rowNum][colNum];

			for (int i = 1; i <= rowNum; i++) {

				XSSFRow row = sheet.getRow(i);
				for (int j = 0; j < colNum; j++) {
					XSSFCell cell = row.getCell(j);
					String CellValue = getCellValueAsString(cell);
					data[i - 1][j] = CellValue;
					System.out.println(data[i - 1][j]);
				}
			}
			return data;
		} finally {
			fis.close();
		}
	}

	public static void main(String[] args) throws IOException {
		ExcelUtil ex = new ExcelUtil();
		// System.out.println(ex.readDataFromExcel("TestData", "Playback"));
		System.out.println(ex.getTestData("Playback", "ALTBALAJI_EPISODE", "DRM"));

	}
}

class Inner {
	private Map<String, Integer> dictCol = new HashMap();
	private Map<String, Integer> dictRow = new HashMap();
	private List<List<String>> mainlist = new ArrayList<List<String>>();

	public synchronized Map<String, Integer> getDictCol() {
		return dictCol;
	}

	public synchronized List<List<String>> getMainlist() {
		return mainlist;
	}

	public synchronized Map<String, Integer> getDictRow() {
		return dictRow;
	}

	public synchronized void setDictCol(HashMap<String, Integer> dictCol) {
		this.dictCol = dictCol;
	}

	public synchronized void setDictRow(HashMap<String, Integer> dictRow) {
		this.dictRow = dictRow;
	}

	public synchronized void setMainlist(List<List<String>> mainlist) {
		this.mainlist = mainlist;
	}

}

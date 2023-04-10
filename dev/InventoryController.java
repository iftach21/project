import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryController {
    private CategoryController CategoryControl;
    private List<Item> ItemsList;

    public InventoryController() {
        this.CategoryControl = new CategoryController();
        this.ItemsList = new ArrayList<Item>();
    }

    public specificItem findSpecificItem(int itemId){
        specificItem currentSpecificItem;
        Item currentItem;
        for (int i = 0; i < ItemsList.size(); i++){
            currentItem = ItemsList.get(i);
            for (int j = 0; j < currentItem.getAmount(); j++){
                currentSpecificItem = currentItem.getSpecificItemList(j);
                if(currentSpecificItem.getItemID() == itemId){
                    return currentSpecificItem;
                }
            }
        }
        return null;
    }

    public Item getItemByCatalogNumber(String itemCatalogNumber) {
        Item currentItem;
        for (int i = 0; i < ItemsList.size(); i++){
            if (ItemsList.get(i).getCatalogNum().equals(itemCatalogNumber)){
                currentItem = ItemsList.get(i);
                return currentItem;
            }
        }
        return null;
    }
    //Method: deleteSpecificItem
    //This method deleted a specific item from the entire inventory
    public boolean deleteSpecificItem(int itemID){
        Item currentItem;
        specificItem currentSpecificItem;
        for (int i = 0; i < ItemsList.size(); i++){
            currentItem = ItemsList.get(i);
            for (int j = 0; j < currentItem.getAmount(); j++){
                currentSpecificItem = currentItem.getSpecificItemList(j);
                if (currentSpecificItem.getItemID() == itemID){
                    currentItem.removeSpecificItem(currentSpecificItem);
                    return true;
                }
            }
        }
        return false;
    }

    //Method: deleteGeneralItem
    //This method deletes a general item and all of it's specific items from the inventory
    public boolean deleteGeneralItem(String catalogNumber){
        Category currentCategory;
        subCategory currentSubCat;
        Item currentItem;
        specificItem currentSpecificItem;

        for (int i = 0; i < CategoryControl.getCategoriesList().size(); i++){
            currentCategory = CategoryControl.getCategoriesList().get(i);
            for (int j = 0; j < currentCategory.getAmount(); j++){
                currentSubCat = currentCategory.getSubCategory(j);
                for (int w = 0; w < currentSubCat.getGeneralItemsList().size(); w++){
                    currentItem = currentSubCat.getGeneralItemsList().get(w);
                    if (currentItem.getCatalogNum().equals(catalogNumber)){
                        currentSubCat.getGeneralItemsList().remove(currentItem);
                        ItemsList.remove(currentItem);
                        currentSubCat.setAmount(currentSubCat.getAmount() - 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean deleteSubCat(String subCategoryName){
        Category currentCategory;
        subCategory currentSubCat;

        for (int i = 0; i < CategoryControl.getCategoriesList().size(); i++){
            currentCategory = CategoryControl.getCategoriesList().get(i);
            for (int j = 0; j < currentCategory.getAmount(); j++){
                currentSubCat = currentCategory.getSubCategory(j);
                if (currentSubCat.getSubCategoryName().equals(subCategoryName)){
                    currentCategory.removeSubCategory(currentSubCat);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteCat(String categoryName){
        Category currentCategory;

        for (int i = 0; i < CategoryControl.getCategoriesList().size(); i++){
            currentCategory = CategoryControl.getCategoriesList().get(i);
            if (currentCategory.getCategoryName().equals(categoryName)){
                CategoryControl.getCategoriesList().remove(currentCategory);
                CategoryControl.setAmount(CategoryControl.getAmount() - 1);
                return true;
            }
        }
        return false;
    }

    public void addGeneralItem(Item generalItem) {
        ItemsList.add(generalItem);
    }

    public CategoryController getCategoryControl() {
        return CategoryControl;
    }

    //Method 4: shortageReportFull
    //This method provides a report for all the products that need to be ordered
    public Report shortageReportFull(){
        //Set variables for method
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Shortage, currentDate);
        String reportInformation = "";
        int defectedCount = 0;
        //Iterate every category
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++){
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            //Iterate every sub-category
            for (int j = 0; j < currentCategory.getAmount(); j++){
                subCategory currentSubCategory = currentCategory.getSubCategory(j);
                //Iterate every general-item
                for (int w = 0; w < currentSubCategory.getAmount(); w++){
                    Item currentItem = currentSubCategory.getItem(w);
                    //Check for each specific item if it is defected, and needs to be added to the order
                    for (int z = 0; z < currentItem.getAmount(); z++){
                        if (currentItem.getSpecificItemList(z).getisDefected()){
                            defectedCount++;
                        }
                    }
                    if (currentItem.getAmount() < currentItem.getMinQuantity() + defectedCount){
                        //Add the information collected to the report data
                        reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                                "Defected amount: " + defectedCount + " Total to order: " +
                                (currentItem.getMinQuantity() - currentItem.getAmount() + defectedCount) + "\n";
                        currentReport.setReportData(reportInformation);
                        //Reset variables
                        reportInformation = "";
                        defectedCount = 0;
                    }
                }
            }
        }
        //If there are no shortages
        if (currentReport.getReportInformation().equals("")){
            currentReport.setReportData("There are no shortages.");
        }
        return currentReport;
    }

    //Method 5: shortageReportCategory
    //This method provides a report for all the products that need to be ordered by a specific category
    public Report shortageReportCategory(String categoryName){
        //Set variables for method
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Shortage, currentDate);
        Category currentCategory = this.CategoryControl.getCategory(categoryName);
        String reportInformation = "";
        int defectedCount = 0;

        for (int j = 0; j < currentCategory.getAmount(); j++){
            subCategory currentSubCategory = currentCategory.getSubCategory(j);
            //Iterate every general-item
            for (int w = 0; w < currentSubCategory.getAmount(); w++){
                Item currentItem = currentSubCategory.getItem(w);
                //Check for each specific item if it is defected, and needs to be added to the order
                for (int z = 0; z < currentItem.getAmount(); z++){
                    if (currentItem.getSpecificItemList(z).getisDefected()){
                        defectedCount++;
                    }
                }
                if (currentItem.getAmount() < currentItem.getMinQuantity() + defectedCount){
                    //Add the information collected to the report data
                    reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                            "Defected amount: " + defectedCount + " Total to order: " +
                            (currentItem.getMinQuantity() - currentItem.getAmount() + defectedCount) + "\n";
                    currentReport.setReportData(reportInformation);
                    //Reset variables
                    reportInformation = "";
                    defectedCount = 0;
                }
            }
        }
        //If there are no shortages
        if (currentReport.getReportInformation().equals("")){
            currentReport.setReportData("There are no shortages.");
        }
        return currentReport;
    }

    //Method 6: shortageReportGeneralItem
    //This method provides a shortage report for a general item
    public Report shortageReportGeneralItem(String catalogNumber){
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Shortage, currentDate);
        Item currentItem;
        String reportInformation = "";
        int defectedCount = 0;
        for (int i = 0; i < this.ItemsList.size(); i++){
            currentItem = this.ItemsList.get(i);
            for (int z = 0; z < currentItem.getAmount(); z++){
                if (currentItem.getSpecificItemList(z).getisDefected()){
                    defectedCount++;
                }
            }
            if (currentItem.getCatalogNum().equals(catalogNumber) &&
                    (currentItem.getAmount() < currentItem.getMinQuantity() + defectedCount)){
                //Add the information collected to the report data
                reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                        "Defected amount: " + defectedCount + " Total to order: " +
                        (currentItem.getMinQuantity() - currentItem.getAmount() + defectedCount) + "\n";
                currentReport.setReportData(reportInformation);
                //Reset variables
                reportInformation = "";
                defectedCount = 0;
            }

        }
        //If there are no shortages
        if (currentReport.getReportInformation().equals("")){
            currentReport.setReportData("There are no shortages.");
        }
        return currentReport;
    }

    public Report priceHistoryReport(String catalogNumber){
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.PriceHistory, currentDate);
        Item currentItem;
        String reportInformation = "";
        for (int i = 0; i < ItemsList.size(); i++){
            currentItem = ItemsList.get(i);
            if (currentItem.getCatalogNum().equals(catalogNumber)){
                reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n";
                for (int j = 0; j < currentItem.getPriceHistorySize(); j++){
                    reportInformation += currentItem.getPriceHistorySpecific(j).toString() + "\n";
                }
                currentReport.setReportData(reportInformation);
                return currentReport;
            }
        }
        //If there are no shortages
        if (currentReport.getReportInformation().equals("")){
            currentReport.setReportData("There is no price history for the product.");
        }
        return currentReport;
    }

    //Method 7: returnTempLevel
    //This method returns a TempLevel given the user's chouse
    public TempLevel returnTempLevel(String userInput){
        if (!userInput.equals("A") && !userInput.equals("B") && !userInput.equals("C")){
            return null;
        } else if (userInput.equals("A")) {
            return TempLevel.regular;
        } else if (userInput.equals("B")) {
            return TempLevel.cold;
        }
        else {
            return TempLevel.frozen;
        }
    }

    @Override
    public String toString() {
        String categoryController = "Inventory - Amount of categories: " + CategoryControl.getAmount() + '\n';
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++){
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            categoryController += currentCategory.toString();
        }
        return categoryController;
    }
    public Report FullCountingReport() {
        //Set variables for method
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Inventory, currentDate);
        String reportInformation = "";
        //Iterate every category
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++){
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            //Iterate every sub-category
            for (int j = 0; j < currentCategory.getAmount(); j++){
                subCategory currentSubCategory = currentCategory.getSubCategory(j);
                //Iterate every general-item
                for (int w = 0; w < currentSubCategory.getAmount(); w++){
                    Item currentItem = currentSubCategory.getItem(w);
                    //Add the information collected to the report data
                    reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                            "Total amount: " + currentItem.getAmount() + "\n";
                    currentReport.setReportData(reportInformation);
                    //Reset variables
                    reportInformation = "";
                }
            }
        }
        return currentReport;
    }

    public Report CategoryCountingReport(String categoryName) {
        //Set variables for method
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Inventory, currentDate);
        Category currentCategory = this.CategoryControl.getCategory(categoryName);
        String reportInformation = "";

        for (int j = 0; j < currentCategory.getAmount(); j++){
            subCategory currentSubCategory = currentCategory.getSubCategory(j);
            //Iterate every general-item
            for (int w = 0; w < currentSubCategory.getAmount(); w++){
                Item currentItem = currentSubCategory.getItem(w);
                //Add the information collected to the report data
                reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                        "Total amount: " + currentItem.getAmount() + "\n";
                currentReport.setReportData(reportInformation);
                //Reset variables
                reportInformation = "";
            }
        }
        return currentReport;
    }


    public Report ItemCountingReport(String catalogNumber)
    {
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Inventory, currentDate);
        Item currentItem;
        String reportInformation = "";
        for (int i = 0; i < this.ItemsList.size(); i++){
            currentItem = this.ItemsList.get(i);
            if (currentItem.getCatalogNum().equals(catalogNumber)){
                //Add the information collected to the report data
                reportInformation += currentItem.getName() + ", Catalog Number: " + currentItem.getCatalogNum() + "\n" +
                        "Total amount: " + currentItem.getAmount() + "\n";
                currentReport.setReportData(reportInformation);
                //Reset variables
                reportInformation = "";
            }
        }
        return currentReport;
    }

    public Report FullDefectiveReport()
    {
        String reportInformation = "";
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return null;
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Defective, currentDate);
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = this.ItemsList.get(i);
            for (int j = 0; j < currentItem.getAmount(); j++)
            {
                specificItem currentSpecificItem = currentItem.getSpecificItemList(j);
                if (currentSpecificItem.getisDefected())
                {
                    //TODO - check if need to do diffrent func for that
//                    currentSpecificItem.setLocation(Location.DefctiveArea);
                    reportInformation += currentSpecificItem.toString() + "\n";
                    currentReport.setReportData(reportInformation);
                    reportInformation = "";
                }
            }
        }
        return currentReport;
    }

    public Report CategoryDefectiveReport(String _CategoryName) {
        String reportInformation = "";
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return null;
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Defective, currentDate);
        boolean flag = false;
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++)
        {
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            if (currentCategory.getCategoryName().equals(_CategoryName))
            {
                flag = true;
                for (int j = 0; j < currentCategory.getAmount(); j++){
                    subCategory currentSubCategory = currentCategory.getSubCategory(j);
                    for (int w = 0; w < currentSubCategory.getAmount(); w++)
                    {
                        Item currentItem = currentSubCategory.getItem(w);
                        for (int k = 0; k < currentItem.getAmount(); k++)
                        {
                            specificItem currentSpecificItem = currentItem.getSpecificItemList(k);
                            if (currentSpecificItem.getisDefected())
                            {
                                //TODO - check if need to do diffrent func for that
//                                currentSpecificItem.setLocation(Location.DefctiveArea);
                                reportInformation += currentSpecificItem.toString() + "\n";
                                currentReport.setReportData(reportInformation);
                                reportInformation = "";
                            }
                        }
                    }
                }
            }
        }
        if (!flag)
        {
            System.out.println("There no such category");
            return null;
        }
        return currentReport;
    }

    public Report ItemDefectiveReport(String CatalogNum)
    {
        String reportInformation = "";
        if (this.ItemsList.size() == 0)
            return null;
        Date currentDate = new Date();
        Report currentReport = new Report(reportType.Defective, currentDate);
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = ItemsList.get(i);
            if (currentItem.getCatalogNum().equals(CatalogNum))
            {
                for (int j = 0; j < currentItem.getAmount(); j++) {

                    specificItem currentSpecificItem = currentItem.getSpecificItemList(j);
                    if (currentSpecificItem.getisDefected()) {
                        //TODO - check if need to do diffrent func for that
                        //                                currentSpecificItem.setLocation(Location.DefctiveArea);
                        reportInformation += currentSpecificItem.toString() + "\n";
                        currentReport.setReportData(reportInformation);
                        reportInformation = "";
                    }
                }
            }
        }
        return currentReport;
    }

    public void FullStandardDiscount(double _amount)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = this.ItemsList.get(i);
            currentItem.getDiscount().setStandardDiscount(_amount);
        }
    }

    public void FullPercentageDiscount(double _amount)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = this.ItemsList.get(i);
            currentItem.getDiscount().setPercentageDiscount(_amount);
        }
    }

    public void CategoryPercentageDiscount(double _amount, String _CategoryName)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = this.ItemsList.get(i);
            if (currentItem.getCategoryName().equals(_CategoryName))

                //TODO - SET CategoryName in Item

                currentItem.getDiscount().setPercentageDiscount(_amount);
        }
    }

    public void CategoryStandardDiscount(double _amount, String _CategoryName)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.ItemsList.size(); i++)
        {
            Item currentItem = this.ItemsList.get(i);
            if (currentItem.getCategoryName().equals(_CategoryName))

                //TODO - SET CategoryName in Item

                currentItem.getDiscount().setStandardDiscount(_amount);
        }
    }

    public void SubCategoryStandardDiscount(double _amount, String _CategoryName, String _Subcategory)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++)
        {
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            if (currentCategory.getCategoryName().equals(_CategoryName))
            {
                for (int j = 0; j < currentCategory.getAmount(); j++) {
                    subCategory currentSubCategory = currentCategory.getSubCategory(j);
                    if (currentSubCategory.getSubCategoryName().equals(_Subcategory)) {
                        for (int w = 0; w < currentSubCategory.getAmount(); w++) {
                            Item currentItem = currentSubCategory.getItem(w);
                            currentItem.getDiscount().setStandardDiscount(_amount);
                        }
                    }
                }
            }
        }
    }

    public void SubCategoryPercentageDiscount(double _amount, String _CategoryName, String _Subcategory)
    {
        if (this.CategoryControl.getCategoriesList().size() == 0)
            return;
        for (int i = 0; i < this.CategoryControl.getCategoriesList().size(); i++)
        {
            Category currentCategory = this.CategoryControl.getCategoriesList().get(i);
            if (currentCategory.getCategoryName().equals(_CategoryName))
            {
                for (int j = 0; j < currentCategory.getAmount(); j++) {
                    subCategory currentSubCategory = currentCategory.getSubCategory(j);
                    if (currentSubCategory.getSubCategoryName().equals(_Subcategory)) {
                        for (int w = 0; w < currentSubCategory.getAmount(); w++) {
                            Item currentItem = currentSubCategory.getItem(w);
                            currentItem.getDiscount().setPercentageDiscount(_amount);
                        }
                    }
                }
            }
        }
    }



}

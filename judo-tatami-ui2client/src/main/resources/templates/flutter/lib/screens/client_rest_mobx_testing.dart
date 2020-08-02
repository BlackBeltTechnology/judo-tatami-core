import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_input_text.dart';
import 'package:judo/screens/order_create_page.dart';
import 'package:judo/screens/order_view_page.dart';
import 'package:judo/store/external/northwind_external_ap_store.dart';
import 'package:judo/store/external/northwind_external_category_info_store.dart';
import 'package:judo/store/internal/northwind_internal_ap_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:provider/provider.dart';

class ClientRestMobxTest extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final northwindExternalApStore =
        Provider.of<NorthwindExternalApStore>(context);
    final northwindInternalApStore =
        Provider.of<NorthwindInternalApStore>(context);

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Row(
          children: [
            JudoButton(
              col: 1,
              label: 'Get categories',
              onPressed: () => northwindExternalApStore.getCategories(),
            ),
            JudoInputText(
              col: 1,
              label: 'Category Name',
              onChanged: (value) =>
                  northwindExternalApStore.editCategory.categoryName = value,
            ),
            JudoButton(
              col: 1,
              label: 'Create category',
              onPressed: () => northwindExternalApStore.createCategory(),
            ),
            Observer(
              builder: (_) =>
                  DropdownButton<NorthwindExternalCategoryInfoStore>(
                hint: Text('select'),
                value: northwindExternalApStore.currentCategory,
                icon: Icon(Icons.arrow_drop_down),
                elevation: 16,
                style: TextStyle(color: kPrimaryColor),
                underline: Container(
                  height: 2,
                  color: kSecondaryColor,
                ),
                onChanged: (NorthwindExternalCategoryInfoStore newValue) {
                  northwindExternalApStore.currentCategory = newValue;
                },
                items: northwindExternalApStore
                    .northwindExternalCategoryInfoStoreList
                    .map<DropdownMenuItem<NorthwindExternalCategoryInfoStore>>(
                        (NorthwindExternalCategoryInfoStore value) {
                  return DropdownMenuItem<NorthwindExternalCategoryInfoStore>(
                    value: value,
                    child: Text(value.categoryName),
                  );
                }).toList(),
              ),
            ),
            JudoButton(
              col: 1,
              label: 'Delete choosed category',
              onPressed: () => northwindExternalApStore.removeCategory(),
            ),
          ],
        ),
        Row(
          children: [
            JudoButton(
              col: 1,
              label: 'Get shippers',
              onPressed: northwindInternalApStore.getShippers,
            ),
            JudoInputText(
              col: 1,
              label: 'Shipper Name',
              onChanged: (value) =>
                  northwindInternalApStore.editShipper.companyName = value,
            ),
            JudoButton(
              col: 1,
              label: 'Create shipper',
              onPressed: northwindInternalApStore.createShipper,
            ),
          ],
        ),
        Row(
          children: [
            JudoButton(
              col: 1,
              label: 'Get products',
              onPressed: northwindExternalApStore.getProducts,
            ),
            JudoInputText(
              col: 1,
              label: 'Product Name',
              onChanged: (value) =>
                  northwindExternalApStore.editProduct.productName = value,
            ),
            JudoInputText(
              col: 1,
              label: 'Product price',
              onChanged: (value) => northwindExternalApStore
                  .editProduct.unitPrice = double.parse(value),
            ),
            JudoInputText(
              col: 1,
              label: 'Product weight',
              onChanged: (value) => northwindExternalApStore
                  .editProduct.weight = double.parse(value),
            ),
            Observer(
              builder: (_) =>
                  DropdownButton<NorthwindExternalCategoryInfoStore>(
                hint: Text('select'),
                value: northwindExternalApStore.editProduct.category,
                icon: Icon(Icons.arrow_drop_down),
                elevation: 16,
                style: TextStyle(color: kPrimaryColor),
                underline: Container(
                  height: 2,
                  color: kSecondaryColor,
                ),
                onChanged: (NorthwindExternalCategoryInfoStore newValue) {
                  northwindExternalApStore.editProduct.category = newValue;
                },
                items: northwindExternalApStore
                    .northwindExternalCategoryInfoStoreList
                    .map<DropdownMenuItem<NorthwindExternalCategoryInfoStore>>(
                        (NorthwindExternalCategoryInfoStore value) {
                  return DropdownMenuItem<NorthwindExternalCategoryInfoStore>(
                    value: value,
                    child: Text(value.categoryName),
                  );
                }).toList(),
              ),
            ),
            JudoButton(
              col: 1,
              label: 'Create product',
              onPressed: northwindExternalApStore.createProduct,
            ),
          ],
        ),
        Row(
          children: [
            JudoButton(
              col: 1,
              label: 'Create order',
              onPressed: () => Navigator.push(context,
                  MaterialPageRoute(builder: (context) => OrderCreatePage())),
            ),
          ],
        ),

//        DataTable(
//          columns: [
//            DataColumn(
//              label: Text('Id'),
//            ),
//            DataColumn(
//              label: Text('Name'),
//            ),
//          ],
//          rows: externalStore.northwindExternalAp.allCategories
//              .map<DataRow>((e) => DataRow(cells: [
//                    DataCell(Observer(builder: (_) => Text(e.categoryName))),
//                    DataCell(Observer(builder: (_) => Text(e.identifier))),
//                  ])),
//        ),

        Container(
          height: 500,
          child: Observer(
            builder: (_) => Container(
              child: DataTable(
                onSelectAll: (b) {},
                sortAscending: true,
                columns: [
                  DataColumn(label: Text('Date')),
                  DataColumn(label: Text('Id')),
                  DataColumn(label: Text('Total'))
                ],
                rows: orderDataRow(context),
              ),
            ),
          ),
        ),

        Container(
          height: 500,
          child: Row(
            children: [
              Expanded(
                flex: 1,
                child: Observer(
                    builder: (_) => ListView.builder(
                        itemCount: northwindExternalApStore
                            .northwindExternalCategoryInfoStoreList.length,
                        itemBuilder: (BuildContext ctxt, int index) {
                          return Text(northwindExternalApStore
                              .northwindExternalCategoryInfoStoreList[index]
                              .categoryName);
                        })),
              ),
              Expanded(
                child: Observer(
                    builder: (_) => ListView.builder(
                          itemCount: northwindInternalApStore
                              .northwindInternalShipperInfoStoreList.length,
                          itemBuilder: (BuildContext ctxt, int index) {
                            return Text(northwindInternalApStore
                                .northwindInternalShipperInfoStoreList[index]
                                .companyName);
                          },
                        )),
              ),
              Expanded(
                child: Observer(
                    builder: (_) => ListView.builder(
                        itemCount: northwindExternalApStore
                            .northwindExternalProductInfoStoreList.length,
                        itemBuilder: (BuildContext ctxt, int index) {
                          return Text(northwindExternalApStore
                              .northwindExternalProductInfoStoreList[index]
                              .productName);
                        })),
              ),
            ],
          ),
        ),
      ],
    );
  }

  List<DataRow> orderDataRow(BuildContext context) {
    final internalApStore = Provider.of<NorthwindInternalApStore>(context);

    List<DataRow> dataRowList =
        internalApStore.northwindInternalInternationalOrderInfoStoreList
            .map<DataRow>((order) => DataRow(
                  cells: [
                    DataCell(
                      Text(
                        order.orderDate.toString(),
                      ),
                      onTap: () {
                        internalApStore.selectOrder(order);
                        Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => OrderViewPage()));
                      },
                    ),
                    DataCell(
                      Text(order.identifier),
                    ),
                    DataCell(
                      Text(order.totalPrice.toString()),
                    ),
                  ],
                ))
            .toList();

    return dataRowList;
  }
}

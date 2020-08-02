import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_input_text.dart';
import 'package:judo/store/internal/northwind_internal_ap_store.dart';
import 'package:judo/store/internal/northwind_internal_shipper_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:provider/provider.dart';

class OrderEditPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final internalApStore = Provider.of<NorthwindInternalApStore>(context);

    return SafeArea(
      child: Scaffold(
        body: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  JudoButton(
                    col: 1,
                    label: 'Save',
                    onPressed: () {
                      internalApStore.updateOrder();
                      Navigator.pop(context);
                    },
                  ),
                  JudoButton(
                    col: 1,
                    label: 'Add new item',
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return AlertDialog(
                            content: Container(
                              height: 500,
                              width: 500,
                              child: Observer(
                                  builder: (_) => ListView.builder(
                                      itemCount: internalApStore
                                          .northwindInternalProductInfoStoreList
                                          .length,
                                      itemBuilder:
                                          (BuildContext ctxt, int index) {
                                        return Row(
                                          children: [
                                            Expanded(
                                              flex: 1,
                                              child: Text(internalApStore
                                                  .northwindInternalProductInfoStoreList[
                                                      index]
                                                  .productName),
                                            ),
                                            Expanded(
                                              flex: 1,
                                              child: Text(internalApStore
                                                  .northwindInternalProductInfoStoreList[
                                                      index]
                                                  .category
                                                  .categoryName),
                                            ),
                                            JudoButton(
                                              col: 1,
                                              label: 'Add',
                                              onPressed: () {
                                                internalApStore.currentProduct =
                                                    internalApStore
                                                            .northwindInternalProductInfoStoreList[
                                                        index];
                                                internalApStore.addOrderItem();
                                              },
                                            ),
                                          ],
                                        );
                                      })),
                            ),
                          );
                        },
                      );
                    },
                  ),
                ],
              ),
              Row(
                children: [
                  JudoInputText(
                    col: 1,
                    label: 'Description',
                    initialValue:
                        internalApStore.editOrderInfo.customsDescription,
                    onChanged: (value) => internalApStore
                        .editOrderInfo.customsDescription = value,
                  ),
                  JudoInputText(
                    col: 1,
                    label: 'Excise Tax',
                    initialValue:
                        internalApStore.editOrderInfo.exciseTax.toString(),
                    onChanged: (value) => internalApStore
                        .editOrderInfo.exciseTax = double.parse(value),
                  ),
                  Observer(
                    builder: (_) =>
                        DropdownButton<NorthwindInternalShipperInfoStore>(
                      hint: Text('select'),
                      value: internalApStore.currentShipper,
                      icon: Icon(Icons.arrow_drop_down),
                      elevation: 16,
                      style: TextStyle(color: kPrimaryColor),
                      underline: Container(
                        height: 2,
                        color: kSecondaryColor,
                      ),
                      onChanged: (NorthwindInternalShipperInfoStore newValue) {
                        internalApStore.changeShipper(newValue);
                        internalApStore.currentShipper = newValue;
                      },
                      items: internalApStore
                          .northwindInternalShipperInfoStoreList
                          .map<
                                  DropdownMenuItem<
                                      NorthwindInternalShipperInfoStore>>(
                              (NorthwindInternalShipperInfoStore value) {
                        return DropdownMenuItem<
                            NorthwindInternalShipperInfoStore>(
                          value: value,
                          child: Text(value.companyName),
                        );
                      }).toList(),
                    ),
                  ),
                ],
              ),
              Row(
                children: [
                  Expanded(
                    child: Container(
                      height: 500,
                      child: Observer(
                          builder: (_) => ListView.builder(
                              itemCount:
                                  internalApStore.editOrderInfo.items.length,
                              itemBuilder: (BuildContext ctxt, int index) {
                                return Row(
                                  children: [
                                    Text(internalApStore.editOrderInfo
                                        .items[index].product.productName),
                                    Text(internalApStore
                                        .editOrderInfo
                                        .items[index]
                                        .product
                                        .category
                                        .categoryName),
                                    Text(internalApStore
                                        .editOrderInfo.items[index].quantity
                                        .toString()),
                                    JudoButton(
                                      col: 1,
                                      icon: Icon(Icons.delete),
                                      onPressed: () => {
                                        internalApStore.deleteOrderItem(
                                            internalApStore
                                                .editOrderInfo.items[index])
                                      },
                                    ),
                                  ],
                                );
                              })),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

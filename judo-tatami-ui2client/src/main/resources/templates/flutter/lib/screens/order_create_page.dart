import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_input_text.dart';
import 'package:judo/store/internal/northwind_internal_ap_store.dart';
import 'package:judo/store/internal/northwind_internal_shipper_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:provider/provider.dart';

class OrderCreatePage extends StatelessWidget {
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
                    label: 'Back',
                    icon: Icon(Icons.arrow_back_ios),
                    onPressed: () => Navigator.pop(context),
                  ),
                  JudoButton(
                    col: 1,
                    label: 'Create order',
                    onPressed: () {
                      internalApStore.createOrder();
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
                                                internalApStore
                                                    .createOrderItem();
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
                    onChanged: (value) => internalApStore
                        .editOrderInfo.customsDescription = value,
                  ),
                  JudoInputText(
                    col: 1,
                    label: 'Excise Tax',
                    onChanged: (value) => internalApStore
                        .editOrderInfo.exciseTax = double.parse(value),
                  ),
                  Observer(
                    builder: (_) =>
                        DropdownButton<NorthwindInternalShipperInfoStore>(
                      hint: Text('select'),
                      value: internalApStore.editOrderInfo.shipper,
                      icon: Icon(Icons.arrow_drop_down),
                      elevation: 16,
                      style: TextStyle(color: kPrimaryColor),
                      underline: Container(
                        height: 2,
                        color: kSecondaryColor,
                      ),
                      onChanged: (NorthwindInternalShipperInfoStore newValue) {
                        internalApStore.editOrderInfo.shipper = newValue;
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
                                    Observer(
                                      builder: (_) => Text(internalApStore
                                          .editOrderInfo.items[index].quantity
                                          .toString()),
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

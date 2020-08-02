const String kExampleJson1 = '''
{
  "type": "JudoColumn",
  "col": 12,
  "children": [
    {
      "type": "JudoRow",
      "col": 12,
      "children": [
        {
          "type": "JudoInputText",
          "col": 4,
          "label": "col-4" ,
          "icon" : "alarm_on"
        },
        {
          "type": "JudoInputText",
          "col": 4,
          "label": "col-4" ,
          "icon" : "alarm_on"
        },
        {
          "type": "JudoInputText",
          "col": 4,
          "label": "col-4" ,
          "icon" : "alarm_on"
        }
      ]
    }
  ]
}
''';

const String kExampleJson2 = '''
{
  "type": "JudoRow",
  "col": 12,
  "children": [
    {
      "type": "JudoColumn",
      "col": 6,
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    },
    {
      "type": "JudoColumn",
      "col": 6,
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    }
  ]
}
''';

const String kExampleJson3 = '''
{
  "type": "JudoRow",
  "col": 12,
  "children": [
    {
      "type": "JudoColumn",
      "col": 6,
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    },
    {
      "type": "JudoTab",
      "col": 6,
      "tabs": [
        {
          "label": "Tab-1",
          "icon": "favorite"
        },
        {
          "label": "Tab-2",
          "icon": "favorite"
        }
      ],
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 3,
              "label": "col-3" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 3,
              "label": "col-3" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    }
  ]
}
''';

const String kExampleJson4 = '''
{
  "type": "JudoRow",
  "col": 12,
  "children": [
    {
      "type": "JudoColumn",
      "col": 6,
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    },
    {
      "type": "JudoTab",
      "col": 6,
      "tabs": [
        {
          "label": "Tab-1",
          "icon": "favorite"
        },
        {
          "label": "Tab-2",
          "icon": "favorite"
        },
        {
          "label": "Tab-3",
          "icon": "alarm_on"
        }
      ],
      "children": [
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoRadioGroup",
              "col": 4,
              "children": [
                {
                  "label": "Radio-1",
                  "col": 2,
                  "value": 0
                },
                {
                  "label": "Radio-2",
                  "col": 2,
                  "value": 1
                }
              ]
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 6,
          "children": [
            {
              "type": "JudoInputText",
              "col": 3,
              "label": "col-3" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 3,
              "label": "col-3" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRadioGroup",
          "col": 4,
          "children": [
            {
              "label": "Radio-1",
              "col": 2,
              "value": 0
            },
            {
              "label": "Radio-2",
              "col": 2,
              "value": 1
            }
          ]
        }
      ]
    }
  ]
}
''';

const String kExampleJson5 = '''
{
  "type": "JudoRow",
  "col": 12,
  "children": [
    {
      "type": "JudoColumn",
      "col": 4,
      "children": [
        {
          "type": "JudoTitle",
          "col": 4,
          "text": "Title-1"
        },
        {
          "type": "JudoRow",
          "col": 4,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 4,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoButton",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 4,
          "children": [
            {
              "type": "JudoButton",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoButton",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 4,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    },
    {
      "type": "JudoTab",
      "col": 8,
      "tabs": [
        {
          "label": "Tab-1",
          "icon": "favorite"
        },
        {
          "label": "Tab-2",
          "icon": "favorite"
        },
        {
          "label": "Tab-3",
          "icon": "alarm_on"
        }
      ],
      "children": [
        {
          "type": "JudoRadioGroup",
          "col": 8,
          "children": [
            {
              "label": "Radio-1",
              "col": 2,
              "value": 0
            },
            {
              "label": "Radio-2",
              "col": 2,
              "value": 1
            },
            {
              "label": "Radio-3",
              "col": 2,
              "value": 2
            },
            {
              "label": "Radio-4",
              "col": 2,
              "value": 3
            },
            {
              "label": "Radio-5",
              "col": 2,
              "value": 4
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 8,
          "children": [
            {
              "type": "JudoInputText",
              "col": 2,
              "label": "col-2" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoRadioGroup",
              "col": 6,
              "children": [
                {
                  "label": "Radio-1",
                  "col": 2,
                  "value": 0
                },
                {
                  "label": "Radio-2",
                  "col": 2,
                  "value": 1
                },
                {
                  "label": "Radio-3",
                  "col": 2,
                  "value": 2
                }
              ]
            }
          ]
        },
        {
          "type": "JudoRow",
          "col": 8,
          "children": [
            {
              "type": "JudoInputText",
              "col": 4,
              "label": "col-4" ,
              "icon" : "alarm_on"
            },
            {
              "type": "JudoInputText",
              "col": 4,
              "label": "col-4" ,
              "icon" : "alarm_on"
            }
          ]
        }
      ]
    }
  ]
}
''';

String getExampleJson(int num) {
  String result = kExampleJson1;
  switch (num) {
    case 1:
      result = kExampleJson1;
      break;
    case 2:
      result = kExampleJson2;
      break;
    case 3:
      result = kExampleJson3;
      break;
    case 4:
      result = kExampleJson4;
      break;
    case 5:
      result = kExampleJson5;
      break;
  }
  return result;
}

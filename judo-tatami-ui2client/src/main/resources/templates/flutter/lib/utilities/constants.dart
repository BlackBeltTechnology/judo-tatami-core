{{log 'Main generation for' (variable application.name) level='info'}}
//////////////////////////////////////////////////////////////////////////////
// G E N E R A T E D    C L A S S
// ------------------------------
// Factory expression: {{{ cleanup template.factoryExpression }}}
// Path expression: {{{ cleanup template.pathExpression }}}
// Overwrite: {{{ cleanup template.overwriteExpression }}}
// Template name: {{ template.templateName }}
// Application: {{ application.name }}

part of judo.utility;

const bool isNavigateToSettingsPage = bool.fromEnvironment("TO_SETTINGS_PAGE");


const USE_REQUEST_BASE_URL_FOR_API = bool.fromEnvironment('USE_REQUEST_BASE_URL', defaultValue: false);
const USE_REQUEST_BASE_URL_FROM_SETTINGS = bool.fromEnvironment('USE_REQUEST_BASE_URL_FROM_SETTINGS', defaultValue: false);

const API_DEFAULT_BASE_URL = String.fromEnvironment('API_DEFAULT_BASE_URL', defaultValue:  'http://localhost:8181');
const API_RELATIVE_PATH = String.fromEnvironment('API_RELATIVE_PATH', defaultValue: '/api/{{ baseUrl application.name }}');

const SHOW_TABLE_ROW_ACTIONS = bool.fromEnvironment('SHOW_TABLE_ROW_ACTIONS', defaultValue: false);

const Map<String, String> filterOperationIcons = {
  'equal' : 'equal',
  'equals' : 'equal',
  'notEqual' : 'notEqualVariant',
  'notEquals' : 'notEqualVariant',
  'lessThan' : 'greaterThan',
  'greaterThan' : 'lessThan',
  'lessOrEqual' : 'greaterThanOrEqual',
  'greaterOrEqual' : 'lessThanOrEqual',
  'matches' : 'formatLetterMatches',
  'contain' : 'contain',
  'beginWith' : 'containStart',
};

const Map<String, String> defaultFilterOperation = {
  'boolean' : 'equals',
  'numeric' : 'equal',
  'string' : 'contain',
  'enumeration' : 'equals',
  'date' : 'greaterThan',
  'dateTime' : 'greaterThan',
};

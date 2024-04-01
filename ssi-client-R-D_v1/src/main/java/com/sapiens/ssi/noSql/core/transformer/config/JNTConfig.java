package com.sapiens.ssi.noSql.core.transformer.config;

import java.util.HashMap;
import java.util.Map;

public class JNTConfig {
	public static Map<String, String> JNTConfigMap = new HashMap<>();

	static {/*
		JNTConfigMap.put("contact", "{\r\n" + 
				"\"CE_ID\":$dedup($count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){\r\n" + 
				"         $v.($toMillis($v.updateDate)) = $max($a.($toMillis(updateDate)))}).contactDateOfBirth,\r\n" + 
				"        $count(contactTelephone)!=0?$filter($filter(contactTelephone, function($v, $i, $a){\r\n" + 
				"        $exists($v.discontinueDate)=false}), function($v, $i, $a){\r\n" + 
				"        $v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))\r\n" + 
				"        }).telephoneNumber,\r\n" + 
				"        $count(contactEmail)!=0?$filter($filter(contactEmail, function($v, $i, $a){\r\n" + 
				"        $exists($v.discontinueDate)=false}),function($v, $i, $a){\r\n" + 
				"        $v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))\r\n" + 
				"        }).email),\r\n" + 
				"\r\n" + 
				"\"fullName\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).(contactFirstName&\" \"&contactMiddleName&\" \"&contactName),\r\n" + 
				"\"contactId\":($count(contactIdentifier)!=0?$filter(contactIdentifier, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).id)&'|101'&'|IDIT',\r\n" + 
				"\"_id\":id,\r\n" + 
				"\"contactNumber\":id,\r\n" + 
				"\"preferredContactMethod\":$count(preferredDeliveryType)!=0?{\"id\":preferredDeliveryType.id,\"description\":preferredDeliveryType.desc},\r\n" + 
				"\"firstName\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactFirstName,\r\n" + 
				"\"middleName\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactMiddleName,\r\n" + 
				"\"lastName\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactName,\r\n" + 
				"\"dateOfBirth\":$count(contactHistory)!=0?$filter(contactHistory, function($v, $i, $a){$v.($toMillis(updateDate)) = $max($a.($toMillis(updateDate)))}).contactDateOfBirth,\r\n" + 
				"\"title\":$count(titleVO)!=0?{\"id\":titleVO.id,\"description\":titleVO.desc},\r\n" + 
				"\"gender\":$count(genderVO)!=0?{\"id\":genderVO.id,\"description\":genderVO.desc},\r\n" + 
				"\"nationality\":$count(nationality)!=0?{\"id\":nationality.id,\"description\":nationality.desc},\r\n" + 
				"\"birthCountry\":$count(birthCountryVO)!=0?{\"id\":birthCountryVO.id,\"description\":birthCountryVO.desc},\r\n" + 
				"\"education\":$count(educationVO)!=0?{\"id\":educationVO.id,\"description\":educationVO.desc},\r\n" + 
				"\"profession\":$count(professionVO)!=0?{\"id\":professionVO.id,\"description\":professionVO.desc},\r\n" + 
				"\"telephoneNumbers\":$count(contactTelephone)!=0?$map(contactTelephone, function($v, $i, $a) {\r\n" + 
				"        {\r\n" + 
				"            \"id\":$v.id,\r\n" + 
				"            \"telephoneNumber\":$v.telephoneNumber,\r\n" + 
				"            \"countryDialCode\":{\r\n" + 
				"                    \"id\":$v.desc,\r\n" + 
				"                    \"description\":$v.extCode},\r\n" + 
				"            \"telephonePrefix\":$v.telephoneExtension,\r\n" + 
				"            \"telephoneType\":{\r\n" + 
				"                    \"id\":$v.id,\r\n" + 
				"                    \"description\":$v.desc}\r\n" + 
				"        }\r\n" + 
				"}),\r\n" + 
				"\"emails\":$count(contactEmail)!=0?$map(contactEmail, function($v, $i, $a) {\r\n" + 
				"   {\r\n" + 
				"       \"id\":$v.id,\r\n" + 
				"       \"email\":$v.email,\r\n" + 
				"       \"emailType\":$count($v.emailTypeVO)!=0?$map($v.emailTypeVO, function($v, $i, $a) {\r\n" + 
				"        {\r\n" + 
				"            \"id\":$v.id,\r\n" + 
				"            \"email\":$v.desc\r\n" + 
				"        }\r\n" + 
				"        })\r\n" + 
				"    }\r\n" + 
				"}),\r\n" + 
				"\"bankAccounts\":$count(contactBankAccount)!=0?$map(contactBankAccount, function($v, $i, $a) {\r\n" + 
				"        {\r\n" + 
				"        \"country\":{\r\n" + 
				"                    \"id\":$v.id,\r\n" + 
				"                    \"description\":$v.desc},\r\n" + 
				"        \"bankName\":$v.bankName,\r\n" + 
				"        \"branchName\":$v.branchName,\r\n" + 
				"        \"accountNumberLastDigits\":$v.accountNrEncrypted\r\n" + 
				"        } }),\r\n" + 
				"		\"addresses\":$count(contactAddress)!=0?$count(contactAddress)!=0?$map(contactAddress, function($v, $i, $a) {\r\n" + 
				"    {\r\n" + 
				"        \"id\":$v.addressVO.id,\r\n" + 
				"        \"street\":$v.addressVO.streetName,\r\n" + 
				"        \"house\":$v.addressVO.houseNr,\r\n" + 
				"        \"city\":$v.addressVO.cityName,\r\n" + 
				"        \"postalCode\":$v.addressVO.zipCode,\r\n" + 
				"        \"addressType\":{\r\n" + 
				"            \"id\":$v.id,\r\n" + 
				"            \"description\":$v.desc},\r\n" + 
				"        \"country\":{\r\n" + 
				"            \"id\":$v.id,\r\n" + 
				"            \"description\":$v.desc}\r\n" + 
				"    } })\r\n" + 
				"}");
		
		JNTConfigMap.put("policy", "{\r\n" + 
				"\"policyId\":id&'|101|IDIT',\r\n" + 
				"\"policyNumber\":id,\r\n" + 
				"\"policyHeaderId\":policyHeaderVO.id&'|101|IDIT',\r\n" + 
				"\"_id\":id,\r\n" + 
				"\"policyHeaderNumber\":policyHeaderVO.id,\r\n" + 
				"\"startDate\":policyStartDate,\r\n" + 
				"\"endDate\":policyEndDate,\r\n" + 
				"\"policyVersion\":policyHeaderVO.updateVersion,\r\n" + 
				"\"activePolicyId\":policyHeaderVO.activePolicyId,\r\n" + 
				"\"lob\":policyLobVO.productVersionLobVO.id,\r\n" + 
				"\"readyForRenewal\":policyHeaderVO.preRenewalFlag,\r\n" + 
				"\"lineOfBusiness\":$count($v.policyLobVO.productVersionLobVO)!=0?{\r\n" + 
				"    \"id\":policyLobVO.productVersionLobVO.id,\r\n" + 
				"    \"description\":policyLobVO.productVersionLobVO.desc},\r\n" + 
				"\"premium_amount\":$sum(policyLobVO.policyLobAssetVO.coverVO.netPremiumForCollVO.amount),\r\n" + 
				"\"currency\":$count(currencyVO)!=0?{\r\n" + 
				"    \"id\":currencyVO.id,\r\n" + 
				"    \"description\":currencyVO.desc},\r\n" + 
				"\"product\":$count(policyHeaderVO.productVO)!=0?{\r\n" + 
				"    \"id\":policyHeaderVO.productVO.id,\r\n" + 
				"    \"description\":policyHeaderVO.productVO.desc},\r\n" + 
				"\"status\":$count(policyHeaderVO.statusCodeVO)!=0?{\r\n" + 
				"    \"id\":policyHeaderVO.statusCodeVO.id,\r\n" + 
				"    \"description\":policyHeaderVO.statusCodeVO.desc},\r\n" + 
				"\"covers\":$count(policyLobVO.policyLobAssetVO.coverVO)!=0?$map(policyLobVO.policyLobAssetVO.coverVO, function($v, $i, $a) {\r\n" + 
				"	{\r\n" + 
				"		\"premiumAmount\":$sum($v.coverExtVOs.premiumForCollAmountVO.amountSystem),\r\n" + 
				"		\"insuranceAmount\" :$sum($v.insuranceAmountVO.amountSystem),\r\n" + 
				"        \"coverType\":$v.coverType,\r\n" + 
				"    \"premium\":$count($v.coverExtVOs.premiumForCollAmountVO)!=0?$map($v.coverExtVOs.premiumForCollAmountVO, function($v, $i, $a) {\r\n" + 
				"   {\r\n" + 
				"       \"amount\":$v.amountSystem,\r\n" + 
				"       \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
				"				   \"id\":$v.currencyVO.id,\r\n" + 
				"				   \"description\":$v.currencyVO.desc\r\n" + 
				"							}\r\n" + 
				"   }\r\n" + 
				"}),\r\n" + 
				"\"insurance_Amount\":$count($v.insuranceAmountVO)!=0?$map($v.insuranceAmountVO, function($v, $i, $a) {\r\n" + 
				"   {\r\n" + 
				"       \"amount\":$v.amountSystem,\r\n" + 
				"       \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
				"				   \"id\":$v.currencyVO.id,\r\n" + 
				"				   \"description\":$v.currencyVO.desc}\r\n" + 
				"   }\r\n" + 
				"}),\r\n" + 
				"\"status\":$count($v.coverStatusVO)!=0?$map($v.coverStatusVO, function($v, $i, $a) {\r\n" + 
				"   {\r\n" + 
				"       \"id\":$v.id,\r\n" + 
				"       \"description\":$v.desc\r\n" + 
				"   }\r\n" + 
				"})\r\n" + 
				"\r\n" + 
				"	}\r\n" + 
				"}),\r\n" + 
				"\"policyHolder\":$count(policyContactVO)!=0?$map($filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}), function($v, $i, $a) {\r\n" + 
				"    {\r\n" + 
				"    \"contactId\":$v.contactId,\r\n" + 
				"    \"contactNumber\":$v.contactId,\r\n" + 
				"    \"firstName\":$v.contactVO.firstName,\r\n" + 
				"    \"middleName\":$v.contactVO.middleName,\r\n" + 
				"    \"lastName\":$v.contactVO.name,\r\n" + 
				"    \"title\":$count($v.contactVO.titleVO)!=0?{\r\n" + 
				"        \"id\":$v.contactVO.titleVO.id,\r\n" + 
				"        \"description\":$v.contactVO.titleVO.desc\r\n" + 
				"            },\r\n" + 
				"	\"profession\":$count($v.contactVO.professionVO)!=0?{\r\n" + 
				"		\"id\":$v.contactVO.professionVO.id,\r\n" + 
				"		\"description\":$v.contactVO.professionVO.desc\r\n" + 
				"			},\r\n" + 
				"    \r\n" + 
				"    \"dateOfBirth\":$v.contactVO.dateOfBirth,\r\n" + 
				"    \"gender\":$count($v.contactVO.genderVO)!=0?{\r\n" + 
				"        \"id\":$v.contactVO.genderVO.id,\r\n" + 
				"        \"description\":$v.contactVO.genderVO.desc\r\n" + 
				"            },\r\n" + 
				"    \"nationality\":$count($v.contactVO.nationality)!=0?{\r\n" + 
				"        \"id\":$v.contactVO.nationality.id,\r\n" + 
				"        \"description\":$v.contactVO.nationality.desc\r\n" + 
				"            },\r\n" + 
				"    \"birthCountry\":$count($v.contactVO.birthCountryVO)!=0?{\r\n" + 
				"        \"id\":$v.contactVO.birthCountryVO.id,\r\n" + 
				"        \"description\":$v.contactVO.birthCountryVO.desc\r\n" + 
				"            },\r\n" + 
				"    \"education\":$count($v.contactVO.educationVO)!=0?$map($v.contactVO.educationVO, function($v, $i, $a) {{\r\n" + 
				"        \"id\":$v.id,\r\n" + 
				"        \"description\":$v.desc\r\n" + 
				"            }}),\r\n" + 
				"    \"telephoneNumbers\":$count($v.contactVO.contactTelephone)!=0?$map($v.contactVO.contactTelephone, function($v, $i, $a) {{\r\n" + 
				"        \"id\":$v.id,\r\n" + 
				"        \"telephoneNumber\":$v.telephoneNumber,\r\n" + 
				"        \"countryDialCode\":$count($v.countryDialCode)!=0?{\r\n" + 
				"            \"id\":$v.countryDialCode.id,\r\n" + 
				"            \"description\":$v.countryDialCode.extCode\r\n" + 
				"                },\r\n" + 
				"        \"telephonePrefix\":$v.telephonePrefix,\r\n" + 
				"        \"telephoneType\":$count($v.telephonePrefixVO)!=0?{\r\n" + 
				"            \"id\":$v.telephonePrefixVO.id,\r\n" + 
				"            \"description\":$v.telephonePrefixVO.extCode\r\n" + 
				"                }\r\n" + 
				"            }}),\r\n" + 
				"    \"emails\":$count($v.contactVO.contactEmail)!=0?$map($v.contactVO.contactEmail, function($v, $i, $a) {{\r\n" + 
				"        \"id\":$v.id,\r\n" + 
				"        \"email\":$v.email,\r\n" + 
				"        \"emailType\":$count($v.emailTypeVO)!=0?{\r\n" + 
				"	        \"id\":$v.emailTypeVO.id,\r\n" + 
				"            \"description\":$v.emailTypeVO.desc\r\n" + 
				"                }\r\n" + 
				"            }\r\n" + 
				"    }),\r\n" + 
				"    \"bankAccounts\":$count($v.contactVO.contactBankAccount)!=0?$map($v.contactVO.contactBankAccount, function($v, $i, $a) {{\r\n" + 
				"        \"country\":$count($v.countryVO)!=0?{\r\n" + 
				"            \"id\":$v.countryVO.id,\r\n" + 
				"            \"description\":$v.countryVO.desc},\r\n" + 
				"        \"bankName\":$v.bankName,\r\n" + 
				"        \"branchName\":$v.branchName,\r\n" + 
				"        \"accountNumberLastDigits\":$v.accountNrEncrypted\r\n" + 
				"            }}),\r\n" + 
				"    \"addresses\":$count($v.contactVO.contactAddress)!=0?$map($v.contactVO.contactAddress, function($v, $i, $a) {{\r\n" + 
				"				\"id\":$v.addressVO.id,\r\n" + 
				"				\"street\":$v.addressVO.streetName,\r\n" + 
				"				\"house\":$v.addressVO.houseNr,\r\n" + 
				"				\"postalCode\":$v.addressVO.zipCode,\r\n" + 
				"				\"addressType\":$count($v.addressTypeVO)!=0?{\r\n" + 
				"						\"id\":$v.addressTypeVO.id,\r\n" + 
				"						\"description\":$v.addressTypeVO.desc\r\n" + 
				"							},\r\n" + 
				"				\"country\":$count($v.addressVO.countryVO)!=0?{\r\n" + 
				"						\"id\":$v.addressVO.countryVO.id,\r\n" + 
				"						\"desc\":$v.addressVO.countryVO.desc\r\n" + 
				"							},\r\n" + 
				"						\"city\":$v.addressVO.cityName\r\n" + 
				"                }})\r\n" + 
				"                            }\r\n" + 
				"\r\n" + 
				"							})\r\n" + 
				"}");
		JNTConfigMap.put("claim", "{	\r\n" + 
				"\"claimId\":id&'|101|IDIT',\r\n" + 
				"\"_id\":id,\r\n" + 
				"\"claimNumber\":claimNumber,\r\n" + 
				"\"isPoliceIntervention\"	:	isPoliceIntervention,\r\n" + 
				"\"policyList\":$count(claimPolicyVOList)!=0?$map(claimPolicyVOList, function($v, $i, $a) {\r\n" + 
				"                {\r\n" + 
				"                    \"id\":$v.policyId,\r\n" + 
				"                    \"description\":$v.leading\r\n" + 
				"                } }),\r\n" + 
				"\"createDate\":insertDate,\r\n" + 
				"\"eventDate\":claimEventDate,\r\n" + 
				"\"eventTime\":claimEventTime,\r\n" + 
				"\"notificationDate\":insertDate,\r\n" + 
				"\"claimEventDescription\":claimEventDescription,\r\n" + 
				"\"claimStatusType\":$count(claimStatusTypeVO)!=0?{\r\n" + 
				"    \"id\":claimStatusTypeVO.id,\r\n" + 
				"    \"description\":claimStatusTypeVO.desc},\r\n" + 
				"\"currency\":$count(claimCurrencyVO)!=0?{\r\n" + 
				"    \"id\":claimCurrencyVO.id,\r\n" + 
				"    \"description\":claimCurrencyVO.desc},\r\n" + 
				"\"causeOfLoss\":$count(claimCauseOfLossVO)!=0?{\r\n" + 
				"    \"id\":claimCauseOfLossVO.id,\r\n" + 
				"    \"description\":claimCauseOfLossVO.desc},\r\n" + 
				"\"eventLocation\":$count(claimEventLocationVO)!=0?{\r\n" + 
				"\"claimLocationType\":claimEventLocationVO.id,\r\n" + 
				"\"country\":$count(claimEventLocationVO.countryVO)!=0?{\r\n" + 
				"    \"id\":claimEventLocationVO.countryVO.id,\r\n" + 
				"    \"description\":claimEventLocationVO.countryVO.desc},\r\n" + 
				"\"address\":$count(claimEventLocationVO.addressVO)!=0?$map(claimEventLocationVO.addressVO, function($v, $i, $a) {\r\n" + 
				"    {\r\n" + 
				"    \"houseNumber\":$v.houseNr,\r\n" + 
				"    \"streetName\":$v.streetName,\r\n" + 
				"    \"city\":$v.cityName,\r\n" + 
				"    \"country\":$count($v.countryVO)!=0?{\r\n" + 
				"        \"id\":$v.countryVO.id,\r\n" + 
				"        \"desc\":$v.countryVO.id}\r\n" + 
				"        }\r\n" + 
				"})\r\n" + 
				"},\r\n" + 
				" \"policyHolder\":$count(claimPolicyVOList)!=0?{\r\n" + 
				"        \"contactNumber\":$count(fullRelatedClaimContacts)!=0?$filter(fullRelatedClaimContacts, function($v, $i, $a){$v.claimContactRoleVO.desc=\"Policyholder\"}).claimContactId,\r\n" + 
				"		\"claimContactRole\":$count(fullRelatedClaimContacts)!=0?{\r\n" + 
				"                        \"id\":$filter(fullRelatedClaimContacts, function($v, $i, $a){$v.claimContactRoleVO.desc=\"Policyholder\"}).claimContactRoleVO.id,\r\n" + 
				"					    \"description\":$filter(fullRelatedClaimContacts, function($v, $i, $a){$v.claimContactRoleVO.desc=\"Policyholder\"}).claimContactRoleVO.desc\r\n" + 
				"                    },\r\n" + 
				"					\"policyHolderName\":$count(fullRelatedClaimContacts)!=0?$filter(fullRelatedClaimContacts, function($v, $i, $a){$v.claimantAssetList.claimPaymentDamagedObjectList.beneficiaryTypeVO.desc = \"Policyholder\"}).claimantAssetList.claimPaymentDamagedObjectList.beneficiaryVO.firstName,\r\n" + 
				"					\"insuredName\":$count(fullRelatedClaimContacts)!=0?$filter(fullRelatedClaimContacts, function($v, $i, $a){$v.claimantAssetList.claimPaymentDamagedObjectList.beneficiaryTypeVO.desc = \"Policyholder\"}).claimantAssetList.claimPaymentDamagedObjectList.beneficiaryVO.firstName\r\n" + 
				"},\r\n" + 
				"\"claimant\":$count(fullRelatedClaimContacts)!=0?$map(fullRelatedClaimContacts, function($v, $i, $a) {\r\n" + 
				"                {\r\n" + 
				"                    \"contactNumber\":$v.claimContactId,\r\n" + 
				"				\"claimContactRole\":$count($v.claimContactRoleVO)!=0?{\"id\":$v.claimContactRoleVO.id,\"description\":$v.claimContactRoleVO.desc},\r\n" + 
				"                \r\n" + 
				"                \"claimantDetails\":{\r\n" + 
				"                \"claimantSide\":$count($v.claimantSideVO)!=0?{\"id\":$v.claimantSideVO.id,\"description\":$v.claimantSideVO.desc},\r\n" + 
				"                \"claimantStatusReason\":$count($v.claimantStatusReasonVO)!=0?{\"id\":$v.claimantStatusReasonVO.id,\"description\":$v.claimantStatusReasonVO.desc},\r\n" + 
				"                \"claimantLiability\":$count($v.claimantLiabilityVO)!=0?{\"id\":$v.claimantLiabilityVO.id,\"description\":$v.claimantLiabilityVO.desc}},\r\n" + 
				"\r\n" + 
				"                \"claimantAsset\":$count($v.claimantAssetList)!=0?$map($v.claimantAssetList, function($v, $i, $a) {\r\n" + 
				"                {\r\n" + 
				"                \"assetId\":$v.policyAssetVO.id,\r\n" + 
				"                \"claimantAssetStatusReason\":$count($v.claimPolicyVOList)!=0?{\"id\":$v.claimantAssetStatusReasonVO.id,\"description\":$v.claimantAssetStatusReasonVO.desc},\r\n" + 
				"                \"claimantAssetStatus\":$count($v.claimPolicyVOList)!=0?{\"id\":$v.claimantAssetStatusVO.id,\"description\":$v.claimantAssetStatusVO.desc},\r\n" + 
				"                \"damages\":$count($v.damageList)!=0?$map($v.damageList, function($v, $i, $a) {\r\n" + 
				"                {\r\n" + 
				"                    \"id\":$v.id,\r\n" + 
				"                    \"malusStateReason\":$count($v.malusStateReasonVO)!=0?{\"id\":$v.malusStateReasonVO.id,\"description\":$v.malusStateReasonVO.desc},\r\n" + 
				"                    \"exGratia\":$count($v.exGratiaVO)!=0?{\"id\":$v.exGratiaVO.id,\"description\":$v.exGratiaVO.desc},\r\n" + 
				"                    \"damageStatus\":$count($v.damageStatusVO)!=0?{\"id\":$v.damageStatusVO.id,\"description\":$v.damageStatusVO.desc},\r\n" + 
				"                    \"waiveMalus\":$count($v.waiveMalusVO)!=0?{\"id\":$v.waiveMalusVO.id,\"description\":$v.waiveMalusVO.desc},\r\n" + 
				"                    \"damageCode\":$count($v.damageCodeVO)!=0?{\"id\":$v.damageCodeVO.id,\"description\":$v.damageCodeVO.desc}\r\n" + 
				"                } }),\r\n" + 
				"                \"damagedObjectAgenda\":$count($v.damagedObjectAgendaStepsList)!=0?$map($v.damagedObjectAgendaStepsList, function($v, $i, $a) {\r\n" + 
				"                {\r\n" + 
				"                    \"id\":$v.id,\r\n" + 
				"                    \"entityNumber\":$v.entityNumber,\r\n" + 
				"                    \"entityId\":$v.entityId,\r\n" + 
				"                    \"agendaStepGroup\":$count($v.agendaStepGroupVO){\"id\":$v.agendaStepGroupVO.id,\"description\":$v.agendaStepGroupVO.desc},\r\n" + 
				"                    \"agendaStepStatus\":$count($v.agendaStepStatusVO){\"id\":$v.agendaStepStatusVO.id,\"description\":$v.agendaStepStatusVO.desc},\r\n" + 
				"                    \"agendaStepDueDate\":$v.agendaStepDueDate,\r\n" + 
				"                    \"agendaStepStatusDate\":$v.agendaStepStatusDate\r\n" + 
				"                \r\n" + 
				"                } })\r\n" + 
				"            } })\r\n" + 
				"    } }),\r\n" + 
				"	\"informant\":$count($filter(fullRelatedClaimContacts, function($v, $i, $a){$v.'@type'=\"claim:ClaimContactVO\"}))!=0?$map($filter(fullRelatedClaimContacts, function($v, $i, $a){$v.'@type'=\"claim:ClaimContactVO\"}), function($v, $i, $a) {\r\n" + 
				"            {\r\n" + 
				"                \"contactNumber\":$v.claimContactId,\r\n" + 
				"                \"claimContactRole\":$count($v.claimContactRoleVO)!=0?{\r\n" + 
				"                    \"id\":$v.claimContactRoleVO.id,\r\n" + 
				"                    \"description\":$v.claimContactRoleVO.desc},\r\n" + 
				"            \"informantDetails\":$count($v.tInformantTypeVO)!=0?$map($v.tInformantTypeVO, function($v, $i, $a) {\r\n" + 
				"            {\r\n" + 
				"                \"informantType\":$count($v)!=0?{\r\n" + 
				"                    \"id\":$v.id,\r\n" + 
				"                    \"description\":$v.desc}\r\n" + 
				"            } })\r\n" + 
				"        } })\r\n" + 
				"}");
		
		
		JNTConfigMap.put("payment", "{\r\n" + 
				"    \"_id\":id,\r\n" + 
				"    \"policyHolderId\":$count(policyContactVO)!=0?$filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}).contactId,\r\n" + 
				"    \"policyHolderName\":$count(policyContactVO)!=0?$filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}).(contactVO.firstName&\" \"&contactVO.MiddleName&\" \"&contactVO.name),\r\n" + 
				"    \"policyNumber\":externalPolicyNr,\r\n" + 
				"    \"product\":$count(policyHeaderVO.productVO)!=0?{\r\n" + 
				"        \"description\":policyHeaderVO.productVO.desc,\r\n" + 
				"        \"id\":policyHeaderVO.productVO.id},\r\n" + 
				"    \"statusDate\":policyHeaderVO.statusDate,\r\n" + 
				"\r\n" + 
				"    \"collection\":$count(paymentSchedulerVO.installmentVO)!=0?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
				"	{\r\n" + 
				"                \"collectionMethod\":{\"description\":$v.collectionMethodVO.desc,\"id\":$v.collectionMethodVO.id},\r\n" + 
				"                \"collectionDate\":$v.collectionDate,\r\n" + 
				"                \"dueDate\":$v.dueDate,\r\n" + 
				"                \"estimatedAmount\":$count($v.estimatedAmountVO)!=0?{\r\n" + 
				"                    \"accountAmount\":$v.estimatedAmountVO.amountSystem,\r\n" + 
				"                    \"amount\":$v.estimatedAmountVO.amount,\r\n" + 
				"                    \"currency\":$count($v.estimatedAmountVO.currencyVO)!=0?{\r\n" + 
				"                        \"description\":$v.estimatedAmountVO.currencyVO.desc,\r\n" + 
				"                        \"id\":$v.estimatedAmountVO.currencyVO.id}\r\n" + 
				"                        },\r\n" + 
				"                \"graceDate\":$v.graceDate\r\n" + 
				"}\r\n" + 
				"}),\r\n" + 
				"    \"installmentDetails\":$count(paymentSchedulerVO.installmentVO)!=0?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
				"	{\r\n" + 
				"    \"basicPremiumAmount\":$count($v.lastInstallmentHistoryVO.installmentDetailsVO)!=0?$map($v.lastInstallmentHistoryVO.installmentDetailsVO,function($v,$i,$a){\r\n" + 
				"        {\r\n" + 
				"            \"id\":$v.id,\r\n" + 
				"            \"accountAmount\":$v.basicPremiumAmountVO.amountSystem,\r\n" + 
				"            \"amount\":$v.basicPremiumAmountVO.amount,\r\n" + 
				"            \"amountRate\":$v.basicPremiumAmountVO.amountRate,\r\n" + 
				"            \"currency\":$count($v.basicPremiumAmountVO.currencyVO)!=0?{\r\n" + 
				"                \"description\":$v.basicPremiumAmountVO.currencyVO.desc,\r\n" + 
				"                \"id\":$v.basicPremiumAmountVO.currencyVO.id},\r\n" + 
				"            \"coverDetails\":$count($v)!=0?{\r\n" + 
				"                \"coverFrom\":$v.coverFrom,\r\n" + 
				"                \"coverTo\":$v.coverTo,\r\n" + 
				"                \"coverId\":$v.coverId\r\n" + 
				"                    }\r\n" + 
				"        }\r\n" + 
				"            }),\r\n" + 
				"        \"taxAmount\":$count($v.taxesAmountVO)!=0?{\r\n" + 
				"            \"amount\":$v.taxesAmountVO.amount,\r\n" + 
				"            \"currency\":$v.taxesAmountVO.currencyVO.desc},\r\n" + 
				"\r\n" + 
				"        \"totalAmount\":$count($v.lastInstallmentHistoryVO.installmentDetailsVO.originalAmountVO)!=0?$map($v.lastInstallmentHistoryVO.installmentDetailsVO.originalAmountVO,function($v,$i,$a){\r\n" + 
				"        {\r\n" + 
				"            \"accountAmount\":$v.amountSystem,\r\n" + 
				"            \"amount\":$v.amount,\r\n" + 
				"            \"amountRate\":$v.amountRate,\r\n" + 
				"            \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
				"                \"description\":$v.currencyVO.desc,\r\n" + 
				"                \"id\":$v.currencyVO.id}\r\n" + 
				"        }\r\n" + 
				"            })\r\n" + 
				"    }\r\n" + 
				"}),\r\n" + 
				"    \"installmentFee\":$count(paymentSchedulerVO.installmentVO)!=0?$map(paymentSchedulerVO.installmentVO,function($v,$i,$a){\r\n" + 
				"        {\r\n" + 
				"        \"accountAmount\":$v.estimatedAmountVO.amountSystem,\r\n" + 
				"            \"amount\":$v.estimatedAmountVO.amount,\r\n" + 
				"            \"currency\":$count($v.estimatedAmountVO.currencyVO)!=0?{\r\n" + 
				"                \"description\":$v.estimatedAmountVO.currencyVO.desc,\r\n" + 
				"                \"id\":$v.estimatedAmountVO.currencyVO.id},\r\n" + 
				"            \"installmentId\":$v.id&'101'&'IDIT',\r\n" + 
				"            \"installmentNumber\":$v.id,\r\n" + 
				"            \"installmentStatus\":$count($v.installmentStatusTypeVO)!=0?{\r\n" + 
				"                \"description\":$v.installmentStatusTypeVO.desc,\r\n" + 
				"                \"id\":$v.installmentStatusTypeVO.id},\r\n" + 
				"            \"installmentType\":$count($v.installmentTypeVO)!=0?{\r\n" + 
				"                \"description\":$v.installmentTypeVO.desc,\r\n" + 
				"                \"id\":$v.installmentTypeVO.id},\r\n" + 
				"            \"originalDueDate\":$v.originalDueDate,\r\n" + 
				"            \"paymentTerms\":$count($v.paymentTermsVO)!=0?{\r\n" + 
				"                \"description\":$v.paymentTermsVO.desc,\r\n" + 
				"                \"id\":$v.paymentTermsVO.id}\r\n" + 
				"            }\r\n" + 
				"            }),\r\n" + 
				"    \"taxes\": $count(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.installmentDetailTaxVOs.installmentDetailTaxAmountVOs)!=0?$map(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.installmentDetailTaxVOs.installmentDetailTaxAmountVOs,function($v,$i,$a){\r\n" + 
				"        {\r\n" + 
				"            \"accountAmount\":$v.amountVO.amountSystem,\r\n" + 
				"            \"amount\":$v.amountVO.amount,\r\n" + 
				"            \"currency\":$count($v.currencyVO)!=0?{\r\n" + 
				"                \"description\":$v.currencyVO.desc,\r\n" + 
				"                \"id\":$v.currencyVO.id}\r\n" + 
				"            }\r\n" + 
				"            }),\r\n" + 
				"    \"totalInstallmentAmount\":(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO)!=0?$map(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO,function($v,$i,$a){\r\n" + 
				"        {\r\n" + 
				"            \"accountAmount\":$v.amountVO.amountSystem,\r\n" + 
				"            \"amount\":$v.amountVO.amount\r\n" + 
				"        }\r\n" + 
				"            }),\r\n" + 
				"    \"totalInstallmentAmount1\":$sum(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.amountVO.amountSystem),\r\n" + 
				"    \"amount1\":$sum(paymentSchedulerVO.installmentVO.lastInstallmentHistoryVO.installmentDetailsVO.amountVO.amount)\r\n" + 
				"\r\n" + 
				"            \r\n" + 
				"}");
		
		JNTConfigMap.put("quote", "statusCodeVO.desc=\"Policy\"?null:({\r\n" + 
				"    \"agentId\":agentId,\r\n" + 
				"    \"_id\":id&\"|101|IDIT\",\r\n" + 
				"    \"endDate\":endorsEndDate,\r\n" + 
				"    \"policyHolderName\":$count(policyContactVO)!=0?$filter(policyContactVO, function($v, $i, $a){$v.policyContactRoleVO.desc=\"Policyholder\"}).contactVO.(firstName&\" \"&name),\r\n" + 
				"    \"status1\":statusCodeVO.desc,\r\n" + 
				"    \"product\":{\r\n" + 
				"        \"description\":policyHeaderVO.productVO.id,\r\n" + 
				"        \"id\":policyHeaderVO.productVO.desc,\r\n" + 
				"        \"proposalNumber\":externalProposalNr,\r\n" + 
				"        \"startDate\":endorsStartDate},\r\n" + 
				"        \"status\":{\"description\":statusCodeVO.desc,\r\n" + 
				"        \"id\":statusCodeVO.id,\r\n" + 
				"        \"validUntil\":endorsEndDate}\r\n" + 
				"})");
	*/}
}

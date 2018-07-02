package com.orange.cepheus.cep.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProviderTest {

	@Test
	public void testHasSameOriginSameProvider() {
		Provider provider = new Provider("http://sameUrl");
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://sameUrl");
		pr.setServiceName("SN");
		pr.setServicePath("SP");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertTrue(checkTest);
	}

	@Test
	public void testHasSameOriginDifferentUrl() {
		Provider provider = new Provider("http://sameUrl");
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://diffUrl");
		pr.setServiceName("SN");
		pr.setServicePath("SP");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertFalse(checkTest);
	}

	@Test
	public void testHasSameOriginDiffServiceName() {
		Provider provider = new Provider("http://sameUrl");
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://sameUrl");
		pr.setServiceName("SN1");
		pr.setServicePath("SP");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertFalse(checkTest);
	}

	@Test
	public void testHasSameOriginDiffServicePath() {
		Provider provider = new Provider("http://sameUrl");
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://sameUrl");
		pr.setServiceName("SN");
		pr.setServicePath("SP1");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertFalse(checkTest);
	}

	@Test
	public void testHasSameOriginUrlNull() {
		Provider provider = new Provider();
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://sameUrl");
		pr.setServiceName("SN");
		pr.setServicePath("SP1");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertFalse(checkTest);
	}

	@Test
	public void testHasSameOriginUrlIsEmpty() {
		Provider provider = new Provider("");
		provider.setServiceName("SN");
		provider.setServicePath("SP");

		Provider pr = new Provider("http://sameUrl");
		pr.setServiceName("SN");
		pr.setServicePath("SP1");
		boolean checkTest = pr.hasSameOrigin(provider);
		assertFalse(checkTest);
	}

	@Test
	public void testHasObjectNull() {
		Provider provider = new Provider();

		Provider pr = null;
		boolean checkTest = provider.hasSameOrigin(pr);
		assertFalse(checkTest);
	}

	@Test
	public void checkObjectIsNull() {
		String a = null;
		String b = null;
		boolean checkTest = Provider.checkEquality(a, b);
		assertTrue(checkTest);
	}

	@Test
	public void checkStringIsNull() {
		String a = null;
		String b = "abc";
		boolean checkTest = Provider.checkEquality(a, b);
		assertFalse(checkTest);
	}

	@Test
	public void checkStringsIsNull() {
		String a = "xyz";
		String b = null;
		boolean checkTest = Provider.checkEquality(a, b);
		assertFalse(checkTest);
	}

	@Test
	public void checkStringsIsEqual() {
		String a = "xyz";
		String b = "xyz";
		boolean checkTest = Provider.checkEquality(a, b);
		assertTrue(checkTest);
	}

	@Test
	public void checkStringIsEqual() {
		String a = "xyz";
		String b = "abc";
		boolean checkTest = Provider.checkEquality(a, b);
		assertFalse(checkTest);
	}
}	

// Copyright (c) 2009, Richard Kennard
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of Richard Kennard nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY RICHARD KENNARD ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL RICHARD KENNARD BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.kennardconsulting.core.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.kennardconsulting.core.net.UrlEncodedQueryString.Separator;

/**
 * Unit tests for UrlEncodedQueryString
 *
 * @author Richard Kennard
 * @version 1.2
 */

public class UrlEncodedQueryStringTests
	extends TestCase {

	public static void main( String[] args ) {

		junit.textui.TestRunner.run( UrlEncodedQueryStringTests.class );
	}

	/**
	 * Test getters
	 */

	public void testGetters() {

		UrlEncodedQueryString queryString = UrlEncodedQueryString.parse( "id=1" );

		assertEquals( "id=1", queryString.toString() );
		assertEquals( "id=1", queryString.toString() );

		queryString = UrlEncodedQueryString.parse( "x=1&y=2" );

		assertEquals( "x=1&y=2", queryString.toString() );
		assertEquals( "x=1;y=2", queryString.toString( Separator.SEMICOLON ) );
		assertEquals( "1", queryString.get( "x" ) );
		assertEquals( "2", queryString.getValues( "y" ).get( 0 ) );
		assertEquals( "2", queryString.getMap().get( "y" ).get( 0 ) );
		assertEquals( queryString.get( "z" ), null );
		assertTrue( !queryString.contains( "z" ) );

		Iterator<String> i = queryString.getNames();
		assertEquals( "x", i.next() );
		assertEquals( "y", i.next() );
		assertTrue( !i.hasNext() );

		// contains

		queryString = UrlEncodedQueryString.parse( "x=1&y=2&z" );
		assertEquals( queryString.get( "z" ), null );
		assertTrue( queryString.contains( "z" ) );
	}

	/**
	 * Test setters
	 */

	public void testSetters()
		throws URISyntaxException {

		// New parameter

		UrlEncodedQueryString queryString = UrlEncodedQueryString.create();
		queryString.set( "forumId", 3 );

		assertEquals( "forumId=3", queryString.toString() );

		queryString.set( "forumId", (Number) null );

		assertEquals( "", queryString.toString() );

		try {
			queryString.set( null, "3" );
			assertTrue( false );
		} catch ( NullPointerException e ) {
			// Should fail
		}

		try {
			queryString.set( null, (String) null );
			assertTrue( false );
		} catch ( NullPointerException e ) {
			// Should fail
		}

		queryString.set( "name", "Richard Kennard" );

		assertEquals( "name=Richard+Kennard", queryString.toString() );

		queryString.append( "name", "Julianne Kennard" );

		assertEquals( "name=Richard+Kennard&name=Julianne+Kennard", queryString.toString() );

		queryString.append( "name", (String) null ).append( null );

		assertEquals( "name=Richard+Kennard&name=Julianne+Kennard&name", queryString.toString() );

		queryString.append( "name=Charlotte+Kennard&name=Millie+Kennard" );

		assertEquals( "name=Richard+Kennard&name=Julianne+Kennard&name&name=Charlotte+Kennard&name=Millie+Kennard", queryString.toString() );

		queryString.set( "name=Charlotte+Kennard;name=Millie+Kennard;add" );

		assertEquals( "name=Charlotte+Kennard&name=Millie+Kennard&add", queryString.toString() );

		queryString.remove( "name" );

		assertEquals( "add", queryString.toString() );

		assertTrue( !queryString.isEmpty() );
		queryString.remove( "add" );
		assertTrue( queryString.isEmpty() );

		queryString = UrlEncodedQueryString.parse( new URI( "http://java.sun.com?a=%3C%3E%26&b=2" ) );

		assertEquals( "<>&", queryString.get( "a" ) );

		Map<String, List<String>> queryMap = queryString.getMap();
		queryMap.get( "a" ).add( 0, "foo" );
		queryMap.put( "b", new ArrayList<String>( Arrays.asList( "3" ) ) );

		// (should not have modified original)

		assertEquals( "a=%3C%3E%26&b=2", queryString.toString() );

		queryString = UrlEncodedQueryString.create( queryString.getMap() );

		assertEquals( "a=%3C%3E%26&b=2", queryString.toString() );

		queryMap.get( "a" ).add( 0, "foo" );
		assertEquals( "a=%3C%3E%26&b=2", queryString.toString() );

		// Test round-trip

		queryString = UrlEncodedQueryString.create();
		queryString.set( "a", "x&y" );
		queryString.set( "b", "u;v" );

		assertEquals( "a=x%26y&b=u%3Bv", queryString.toString() );

		queryString = UrlEncodedQueryString.parse( queryString.toString() );
		assertEquals( "x&y", queryString.get( "a" ) );
		assertEquals( "u;v", queryString.get( "b" ) );
	}

	/**
	 * Test apply
	 */

	public void testApply()
		throws URISyntaxException {

		URI uri = new URI( "http://java.sun.com?page=1" );
		UrlEncodedQueryString queryString = UrlEncodedQueryString.parse( uri );
		queryString.set( "page", 2 );
		uri = queryString.apply( uri );

		assertEquals( "http://java.sun.com?page=2", uri.toString() );

		uri = new URI( "/forum.jsp?message=12" );
		queryString = UrlEncodedQueryString.parse( uri ).append( "reply", 2 );
		uri = queryString.apply( uri );

		assertEquals( "/forum.jsp?message=12&reply=2", uri.toString() );

		// Test escaping

		uri = new URI( "http://www.google.com/search?q=foo+bar" );
		queryString = UrlEncodedQueryString.parse( uri );
		queryString.set( "q", "100%" );
		uri = queryString.apply( uri );
		assertEquals( "http://www.google.com/search?q=100%25", uri.toString() );

		queryString.append( "%", "%25" );
		uri = queryString.apply( uri );
		assertEquals( "http://www.google.com/search?q=100%25&%25=%2525", uri.toString() );

		queryString.set( "q", "a + b = 100%" );
		queryString.remove( "%" );
		uri = queryString.apply( uri );
		assertEquals( "http://www.google.com/search?q=a+%2B+b+%3D+100%25", uri.toString() );

		// Test different parts of the URI

		uri = new URI( "http://rkennard@java.sun.com:80#bar" );
		uri = queryString.apply( uri );
		assertEquals( "http://rkennard@java.sun.com:80?q=a+%2B+b+%3D+100%25#bar", uri.toString() );

		uri = new URI( "http", "userinfo", "::192.9.5.5", 8080, "/path", "query", "fragment" );
		uri = queryString.apply( uri );
		assertEquals( "http://userinfo@[::192.9.5.5]:8080/path?q=a+%2B+b+%3D+100%25#fragment", uri.toString() );

		uri = new URI( "http", "userinfo", "[::192.9.5.5]", 8080, "/path", "query", "fragment" );
		uri = queryString.apply( uri );
		assertEquals( "http://userinfo@[::192.9.5.5]:8080/path?q=a+%2B+b+%3D+100%25#fragment", uri.toString() );

		uri = new URI( "file", "/authority", null, null, null );
		uri = queryString.apply( uri );
		assertEquals( "file:///authority?q=a+%2B+b+%3D+100%25", uri.toString() );
	}

	/**
	 * Test equals
	 */

	public void testEquals()
		throws Exception {

		URI uri = new URI( "http://java.sun.com?page=1&para=2" );
		UrlEncodedQueryString queryString = UrlEncodedQueryString.parse( uri );
		assertEquals( queryString, queryString );
		assertTrue( !queryString.equals( uri ) );

		UrlEncodedQueryString queryString2 = UrlEncodedQueryString.create();
		assertTrue( !queryString.equals( queryString2 ) );
		assertTrue( !queryString2.equals( queryString ) );

		queryString2 = UrlEncodedQueryString.parse( uri.getQuery() );
		assertEquals( queryString, queryString2 );

		assertTrue( queryString.hashCode() == queryString2.hashCode() );

		queryString.set( "page", 2 );
		assertTrue( !queryString.equals( queryString2 ) );
		assertTrue( queryString.hashCode() != queryString2.hashCode() );

		queryString = UrlEncodedQueryString.create();
		queryString2 = UrlEncodedQueryString.create();

		assertEquals( queryString, queryString2 );
		assertTrue( queryString.hashCode() == queryString2.hashCode() );
	}

	/**
	 * Test round-trip
	 */

	public void testRoundTrip()
		throws Exception {

		assertEquals( "page=1&para=2", UrlEncodedQueryString.parse( "page=1&para=2" ).toString() );
		assertEquals( "bar=&baz", UrlEncodedQueryString.parse( "bar=&baz" ).toString() );
		assertEquals( "bar=1&bar=2&bar&bar=&bar=3", UrlEncodedQueryString.parse( "bar=1&bar=2&bar&bar=&bar=3" ).toString() );
	}

	public void testUrlEncodedParameterNames() {

		assertEquals( "page=1&para=2", UrlEncodedQueryString.parse( "%70age=1&par%61=2" ).toString() );
	}
}

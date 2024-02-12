package org.demyo.utils.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link DIOUtils}.
 */
class DIOUtilsTest {
	/**
	 * Tests {@link DIOUtils#closeQuietly(Closeable)}.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyCloseableNormal() throws IOException {
		DIOUtils.closeQuietly((Closeable) null);
		Closeable closeable = Mockito.mock(Closeable.class);
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link DIOUtils#closeQuietly(Closeable)} when the closeable throws an Exception.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyCloseableException() throws IOException {
		Closeable closeable = Mockito.mock(Closeable.class);
		Mockito.doThrow(IOException.class).when(closeable).close();
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link DIOUtils#closeQuietly(ZipFile)}.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyZipFileNormal() throws IOException {
		DIOUtils.closeQuietly((ZipFile) null);
		ZipFile closeable = Mockito.mock(ZipFile.class);
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link DIOUtils#closeQuietly(ZipFile)} when the closeable throws an Exception.
	 *
	 * @throws IOException should never be thrown.
	 */
	@Test
	void testCloseQuietlyZipFileException() throws IOException {
		ZipFile closeable = Mockito.mock(ZipFile.class);
		Mockito.doThrow(IOException.class).when(closeable).close();
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link DIOUtils#closeQuietly(XMLStreamWriter)}.
	 *
	 * @throws XMLStreamException should never be thrown.
	 */
	@Test
	void testCloseQuietlyXMLStreamWriterNormal() throws XMLStreamException {
		DIOUtils.closeQuietly((XMLStreamWriter) null);
		XMLStreamWriter closeable = Mockito.mock(XMLStreamWriter.class);
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link DIOUtils#closeQuietly(XMLStreamWriter)} when the closeable throws an Exception.
	 *
	 * @throws XMLStreamException should never be thrown.
	 */
	@Test
	void testCloseQuietlyXMLStreamWriterException() throws XMLStreamException {
		XMLStreamWriter closeable = Mockito.mock(XMLStreamWriter.class);
		Mockito.doThrow(XMLStreamException.class).when(closeable).close();
		DIOUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}
}

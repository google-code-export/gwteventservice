/*
 * GWTEventService
 * Copyright (c) 2010, GWTEventService Committers
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.novanic.eventservice.test.testhelper;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author sstrohschein
 *         <br>Date: 28.04.2010
 *         <br>Time: 21:45:52
 */
public class DummyServletOutputStream extends ServletOutputStream
{
    private OutputStream nyOutputStream;

    public DummyServletOutputStream(OutputStream anOutputStream) {
        nyOutputStream = anOutputStream;
    }

    public void write(int b) throws IOException {
        nyOutputStream.write(b);
    }

    public void print(String s) throws IOException {
        nyOutputStream.write(s.getBytes());
    }

    public void print(boolean b) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void print(char c) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void print(int i) throws IOException {
        nyOutputStream.write(i);
    }

    public void print(long l) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void print(float f) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void print(double d) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void println() throws IOException {
        nyOutputStream.write("\n".getBytes());
    }

    public void println(String s) throws IOException {
        nyOutputStream.write(s.getBytes());
    }

    public void println(boolean b) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void println(char c) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void println(int i) throws IOException {
        nyOutputStream.write(i);
    }

    public void println(long l) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void println(float f) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void println(double d) throws IOException {
        throw new IOException("Not supported by the dummy \"" + DummyServletOutputStream.class.getName() + "\"!");
    }

    public void write(byte[] b) throws IOException {
        nyOutputStream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        nyOutputStream.write(b, off, len);
    }

    public void flush() throws IOException {
        nyOutputStream.flush();
    }

    public void close() throws IOException {
        nyOutputStream.close();
    }
}
/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
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
package org.jboss.spring.vfs;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.AbstractResource;

/**
 * VFS based Resource.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class VFSResource extends AbstractResource
{
   private VirtualFile file;

   public VFSResource(VirtualFile file)
   {
      if (file == null)
         throw new IllegalArgumentException("Null file");
      this.file = file;
   }

   public VFSResource(URL url)
   {
      if (url == null)
         throw new IllegalArgumentException("Null url");
      try
      {
         this.file = VFS.getChild(url);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Cannot retrieve file from URL: ", e);
      }
   }

   public boolean exists()
   {
      return file.exists();
   }

   public boolean isOpen()
   {
      return false;
   }

   public boolean isReadable()
   {
      return file.getSize() > 0;
   }

   public long lastModified()
   {
      return file.getLastModified();
   }

   public URL getURL() throws IOException
   {
      return file.toURL();
   }

   public URI getURI() throws IOException
   {
      try
      {
         return file.toURI();
      }
      catch (URISyntaxException e)
      {
         IOException ioe = new IOException(e.getMessage());
         ioe.initCause(e);
         throw ioe;
      }
   }

   public File getFile() throws IOException
   {
      return file.getPhysicalFile();
   }

   @SuppressWarnings("deprecation")
   public Resource createRelative(String relativePath) throws IOException
   {
      //VirtualFile.findChild will not scan the parent or current directory
      if (relativePath.startsWith(".") || relativePath.indexOf("/")==-1)
         return new VFSResource(getChild(new URL(getURL(), relativePath)));
      else
         return new VFSResource(file.getChild(relativePath));
   }

   public String getFilename()
   {
      return file.getName();
   }

   public String getDescription()
   {
      return file.toString();
   }

   public InputStream getInputStream() throws IOException
   {
      return file.openStream();
   }

   public String toString()
   {
      return getDescription();
   }

   @Override
   public boolean equals(Object other)
   {
      if (other instanceof VFSResource)
      {
         return file.equals(((VFSResource)other).file);
      }
      else
      {
         return false;
      }
   }

   @Override
   public int hashCode()
   {
      return file.hashCode();
   }

   static VirtualFile getChild(URL url) throws IOException
   {
      try
      {
         return VFS.getChild(url);
      }
      catch (URISyntaxException e)
      {
         IOException ioe = new IOException(e.getMessage());
         ioe.initCause(e);
         throw ioe;
      }
   }
}

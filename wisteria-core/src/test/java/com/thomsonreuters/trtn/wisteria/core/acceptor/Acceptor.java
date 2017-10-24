/**
 * 
 */
package com.thomsonreuters.trtn.wisteria.core.acceptor;

import java.io.IOException;

/**
 * 
 *
 * @author <a href="mailto:alan.xu@thomsonreuters.com">Alan Xu</a>
 * 
 */
public interface Acceptor{
	void bind() throws IOException;
	void close() throws IOException;
}

/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingList;

/**
 * Class that represents an error list.
 * 
 * @author Otávio Scherer Garcia
 */
@Vetoed
public class ErrorList extends ForwardingList<Message> {

	private static Function<Message, String> byCategory = new Function<Message, String>() {
		@Override
		public String apply(Message input) {
			return input.getCategory();
		}
	};

	private final List<Message> delegate;
	private Map<String, Collection<Message>> grouped;

	public ErrorList(List<Message> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Return messages grouped by category. This method can useful if you want to get messages for a specific
	 * category.
	 */
	public Map<String, Collection<Message>> getGrouped() {
		if (grouped == null) {
			grouped = FluentIterable.from(delegate).index(byCategory).asMap();
		}
		return grouped;
	}

	/**
	 * Return all messages by category. This method can useful if you want to get messages from a specific
	 * category.
	 */
	public ErrorListItem from(final String category) {
		List<String> messages = FluentIterable.from(delegate)
			.filter(byCategory(category))
			.transform(toMessageString())
			.toList();

		return new ErrorListItem(messages);
	}

	private Predicate<Message> byCategory(final String category) {
		return new Predicate<Message>() {
			@Override
			public boolean apply(Message input) {
				return input.getCategory().equals(category);
			}
		};
	}

	private Function<Message, String> toMessageString() {
		return new Function<Message, String>() {
			@Override
			public String apply(Message input) {
				return input.getMessage();
			}
		};
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}

	public class ErrorListItem extends ForwardingList<String> {
		private final List<String> delegate;

		public ErrorListItem(List<String> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected List<String> delegate() {
			return delegate;
		}

		@Override
		public String toString() {
			return join(", ");
		}

		public String join(String separator) {
			return Joiner.on(separator).join(delegate);
		}
	}
}

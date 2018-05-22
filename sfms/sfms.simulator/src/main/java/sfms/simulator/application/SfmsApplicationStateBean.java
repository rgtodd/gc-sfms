package sfms.simulator.application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient.ListTopicsPagedResponse;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sfms.common.Constants;

public class SfmsApplicationStateBean implements ApplicationListener<ApplicationReadyEvent> {

	private final Logger logger = Logger.getLogger(SfmsApplicationStateBean.class.getName());

	ManagedChannel m_channel;
	private Subscriber m_subscriber;

	public void close() {
		logger.info("Closing...");
		// stop receiving messages
		if (m_subscriber != null) {
			m_subscriber.stopAsync();
		}

		closeChannel();
	}

	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {

		try {
			logger.info("Starting consumer.");
			startConsumer();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error starting consumer.", e);
		}
	}

	private void startConsumer() {
		openChannel();

		// Create topic if it doesn't exist.
		//
		try (TopicAdminClient topicAdminClient = createTopicAdminClient()) {
			if (!topicExists(topicAdminClient)) {
				logger.info("Creating topic.");
				createTopic(topicAdminClient);
			} else {
				logger.info("Topic already exists.");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error managing topics.", e);
		}

		// Create topic if it doesn't exist.
		//
		try (SubscriptionAdminClient SubscriptionAdminClient = createSubscriptionAdminClient()) {
			if (!subscriptionExists(SubscriptionAdminClient)) {
				logger.info("Creating Subscription.");
				createSubscription(SubscriptionAdminClient);
			} else {
				logger.info("Subscription already exists.");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error managing Subscriptions.", e);
		}

		// Instantiate an asynchronous message receiver
		MessageReceiver receiver = new MessageReceiver() {
			@Override
			public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
				// handle incoming message, then ack/nack the received message
				System.out.println("Id : " + message.getMessageId());
				System.out.println("Data : " + message.getData().toStringUtf8());
				consumer.ack();
			}
		};

		try {
			// Create a subscriber for "my-subscription-id" bound to the message receiver
			logger.info("Creating subscriber.");
			m_subscriber = createSubscriber(receiver);
			m_subscriber.startAsync();
			logger.info("Subscriber created.");
			// ...
		} finally {
			// stop receiving messages
			if (m_subscriber != null) {
				m_subscriber.stopAsync();
			}
		}
	}

	private String getProjectName() {
		return ProjectName.format(Constants.PUBSUB_PROJECT_ID);
	}

	private ProjectTopicName getTopicName() {
		ProjectTopicName topicName = ProjectTopicName.of(Constants.PUBSUB_PROJECT_ID,
				Constants.PUBSUB_CONTROL_TOPIC);
		return topicName;
	}

	private ProjectSubscriptionName getSubscriptionName() {
		ProjectSubscriptionName topicName = ProjectSubscriptionName.of(Constants.PUBSUB_PROJECT_ID,
				Constants.PUBSUB_CONTROL_SUBSCRIPTION);
		return topicName;
	}

	private void createTopic(TopicAdminClient client) {

		client.createTopic(getTopicName());
	}

	private void createSubscription(SubscriptionAdminClient client) {

		client.createSubscription(getSubscriptionName(), getTopicName(), PushConfig.getDefaultInstance(), 0);
	}

	private boolean topicExists(TopicAdminClient client) {

		String projectName = getProjectName();
		String topicName = getTopicName().toString();

		logger.info("Listing topics for " + projectName);
		ListTopicsPagedResponse response = client.listTopics(projectName);
		Iterable<Topic> topics = response.iterateAll();
		for (Topic topic : topics) {
			logger.info("Topic name = " + topic.getName());
			if (topic.getName().equals(topicName)) {
				return true;
			}
		}

		return false;
	}

	private boolean subscriptionExists(SubscriptionAdminClient client) {

		String projectName = getProjectName();
		String subscriptionName = getSubscriptionName().toString();

		logger.info("Listing subscriptions for " + projectName);
		ListSubscriptionsPagedResponse response = client.listSubscriptions(projectName);
		Iterable<Subscription> subscriptions = response.iterateAll();
		for (Subscription subscription : subscriptions) {
			logger.info("Subscription name = " + subscription.getName());
			if (subscription.getName().equals(subscriptionName)) {
				return true;
			}
		}

		return false;
	}

	private TopicAdminClient createTopicAdminClient() throws IOException {

		if (m_channel == null) {
			return TopicAdminClient.create();
		}

		TransportChannelProvider channelProvider = FixedTransportChannelProvider
				.create(GrpcTransportChannel.create(m_channel));
		CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

		return TopicAdminClient.create(TopicAdminSettings.newBuilder()
				.setTransportChannelProvider(channelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build());
	}

	private SubscriptionAdminClient createSubscriptionAdminClient() throws IOException {

		if (m_channel == null) {
			return SubscriptionAdminClient.create();
		}

		TransportChannelProvider channelProvider = FixedTransportChannelProvider
				.create(GrpcTransportChannel.create(m_channel));
		CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

		return SubscriptionAdminClient.create(SubscriptionAdminSettings.newBuilder()
				.setTransportChannelProvider(channelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build());
	}

	private Subscriber createSubscriber(MessageReceiver receiver) {

		if (m_channel == null) {
			return Subscriber.newBuilder(getSubscriptionName(), receiver).build();
		}

		TransportChannelProvider channelProvider = FixedTransportChannelProvider
				.create(GrpcTransportChannel.create(m_channel));
		CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

		return Subscriber.newBuilder(getSubscriptionName(), receiver)
				.setChannelProvider(channelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build();
	}

	private void openChannel() {
		String hostport = System.getenv("PUBSUB_EMULATOR_HOST");
		if (StringUtils.isEmpty(hostport)) {
			return;
		}

		m_channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
	}

	private void closeChannel() {
		if (m_channel != null) {
			m_channel.shutdown();
		}
	}
}

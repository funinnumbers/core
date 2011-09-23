package eu.funinnumbers.db.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardedConfiguration;
import org.hibernate.shards.cfg.ConfigurationToShardConfigurationAdapter;
import org.hibernate.shards.cfg.ShardConfiguration;
import org.hibernate.shards.loadbalance.RoundRobinShardLoadBalancer;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.shards.strategy.ShardStrategyImpl;
import org.hibernate.shards.strategy.access.SequentialShardAccessStrategy;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.resolution.AllShardsShardResolutionStrategy;
import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.RoundRobinShardSelectionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;
import eu.funinnumbers.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods related to Hibernate.
 */
public final class HibernateUtil {

    /**
     * A static SessionFactory variable.
     */
    private static SessionFactory sessionFactory = null; //NOPMD

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static HibernateUtil ourInstance = null;

    /**
     * The Configuration of hibernate.
     */
    private static Configuration configuration = new Configuration().configure();

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private HibernateUtil() {
        try {
            // Create the SessionFactory from hibernateMM.cfg.xml
            sessionFactory = createSessionFactory();


        } catch (Exception ex) {
            // Make sure you log the exception, as it might be swallowed
            Logger.getInstance().debug("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * HibernateUtili is loaded on the first execution of HibernateUtili.getInstance()
     * or the first access to HibernateUtili.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static HibernateUtil getInstance() {
        synchronized (HibernateUtil.class) {
            if (ourInstance == null) {
                ourInstance = new HibernateUtil();
            }
        }

        return ourInstance;

    }


    /**
     * Retuns the SessionFactory.
     *
     * @return the SessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Returns the current Session.
     *
     * @return the current Session
     */
    public Session getSession() {
        Session ses = sessionFactory.getCurrentSession();

        if (!ses.isOpen()) {
            ses = sessionFactory.openSession();
        }

        return ses;
    }

    /**
     * Try to create a SessionFactory.
     *
     * @return the SessionFactory
     */
    public SessionFactory createSessionFactory() {
        SessionFactory sFactory = null;

        try {
            // Create the SessionFactory from hibernateMM.cfg.xml
            sFactory = configuration.buildSessionFactory();

        } catch (Exception ex) {
            // Make sure you log the exception, as it might be swallowed
            Logger.getInstance().debug("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }

        return sFactory;
    }

    /**
     * Set the Hibernate configuration object.
     *
     * @param cfg the configuration file
     */
    public static void setConfiguration(final Configuration cfg) {
        HibernateUtil.configuration = cfg;
    }

    /**
     * Configure Hibernate from the given file.
     *
     * @param cfgFile the configuration file
     */
    public static void setConfiguration(final String cfgFile) {
        HibernateUtil.configuration = new Configuration().configure(cfgFile);
    }

    /**
     * Create the SessionFactory so that can support shards.
     *
     * @return the SessionFactory
     */
    public SessionFactory createShardSessionFactory() {
        final Configuration prototypeConfig = new Configuration().configure("eu/funinnumbers/hibernate0.cfg.xml");
        final List<ShardConfiguration> shardConfigs = new ArrayList<ShardConfiguration>();
        shardConfigs.add(buildShardConfig("eu/funinnumbers/hibernate0.cfg.xml"));
        shardConfigs.add(buildShardConfig("eu/funinnumbers/hibernate1.cfg.xml"));
        final ShardStrategyFactory shardStrFactory = buildShardStrategyFactory();
        final ShardedConfiguration shardedConfig =
                new ShardedConfiguration(prototypeConfig, shardConfigs, shardStrFactory);
        return shardedConfig.buildShardedSessionFactory();
    }

    /**
     * Build the Shard Strategy.
     *
     * @return the ShardStrategyFactory
     */
    ShardStrategyFactory buildShardStrategyFactory() {
        return new ShardStrategyFactory() {
            public ShardStrategy newShardStrategy(final List<ShardId> shardIds) {
                final RoundRobinShardLoadBalancer loadBalancer = new RoundRobinShardLoadBalancer(shardIds);
                final ShardSelectionStrategy pss = new RoundRobinShardSelectionStrategy(loadBalancer);
                final ShardResolutionStrategy prs = new AllShardsShardResolutionStrategy(shardIds);
                final ShardAccessStrategy pas = new SequentialShardAccessStrategy();
                return new ShardStrategyImpl(pss, prs, pas);
            }
        };
    }

    /**
     * Builds ShardConfiguration from the configFile.
     *
     * @param configFile the configuration file
     * @return the ShardedConfiguration
     */
    ShardConfiguration buildShardConfig(final String configFile) {
        final Configuration config = new Configuration().configure(configFile);
        return new ConfigurationToShardConfigurationAdapter(config);
    }


}

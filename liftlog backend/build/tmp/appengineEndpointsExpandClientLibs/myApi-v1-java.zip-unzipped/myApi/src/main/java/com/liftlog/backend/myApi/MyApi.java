/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-10-06 at 19:44:06 UTC 
 * Modify at your own risk.
 */

package com.liftlog.backend.myApi;

/**
 * Service definition for MyApi (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MyApiRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class MyApi extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.20.0 of the myApi library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://myApplicationId.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "myApi/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public MyApi(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  MyApi(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "selectExercises".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link SelectExercises#execute()} method to invoke the remote operation.
   *
   * @param username
   * @return the request
   */
  public SelectExercises selectExercises(java.lang.String username) throws java.io.IOException {
    SelectExercises result = new SelectExercises(username);
    initialize(result);
    return result;
  }

  public class SelectExercises extends MyApiRequest<com.liftlog.backend.myApi.model.ExerciseAPICollection> {

    private static final String REST_PATH = "selectExercises/{username}";

    /**
     * Create a request for the method "selectExercises".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link SelectExercises#execute()} method to invoke the remote operation.
     * <p> {@link SelectExercises#initialize(com.google.api.client.googleapis.services.AbstractGoogleC
     * lientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param username
     * @since 1.13
     */
    protected SelectExercises(java.lang.String username) {
      super(MyApi.this, "POST", REST_PATH, null, com.liftlog.backend.myApi.model.ExerciseAPICollection.class);
      this.username = com.google.api.client.util.Preconditions.checkNotNull(username, "Required parameter username must be specified.");
    }

    @Override
    public SelectExercises setAlt(java.lang.String alt) {
      return (SelectExercises) super.setAlt(alt);
    }

    @Override
    public SelectExercises setFields(java.lang.String fields) {
      return (SelectExercises) super.setFields(fields);
    }

    @Override
    public SelectExercises setKey(java.lang.String key) {
      return (SelectExercises) super.setKey(key);
    }

    @Override
    public SelectExercises setOauthToken(java.lang.String oauthToken) {
      return (SelectExercises) super.setOauthToken(oauthToken);
    }

    @Override
    public SelectExercises setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SelectExercises) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SelectExercises setQuotaUser(java.lang.String quotaUser) {
      return (SelectExercises) super.setQuotaUser(quotaUser);
    }

    @Override
    public SelectExercises setUserIp(java.lang.String userIp) {
      return (SelectExercises) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String username;

    /**

     */
    public java.lang.String getUsername() {
      return username;
    }

    public SelectExercises setUsername(java.lang.String username) {
      this.username = username;
      return this;
    }

    @Override
    public SelectExercises set(String parameterName, Object value) {
      return (SelectExercises) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "selectLifts".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link SelectLifts#execute()} method to invoke the remote operation.
   *
   * @param username
   * @return the request
   */
  public SelectLifts selectLifts(java.lang.String username) throws java.io.IOException {
    SelectLifts result = new SelectLifts(username);
    initialize(result);
    return result;
  }

  public class SelectLifts extends MyApiRequest<com.liftlog.backend.myApi.model.LiftAPICollection> {

    private static final String REST_PATH = "selectLifts/{username}";

    /**
     * Create a request for the method "selectLifts".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link SelectLifts#execute()} method to invoke the remote operation. <p>
     * {@link
     * SelectLifts#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param username
     * @since 1.13
     */
    protected SelectLifts(java.lang.String username) {
      super(MyApi.this, "POST", REST_PATH, null, com.liftlog.backend.myApi.model.LiftAPICollection.class);
      this.username = com.google.api.client.util.Preconditions.checkNotNull(username, "Required parameter username must be specified.");
    }

    @Override
    public SelectLifts setAlt(java.lang.String alt) {
      return (SelectLifts) super.setAlt(alt);
    }

    @Override
    public SelectLifts setFields(java.lang.String fields) {
      return (SelectLifts) super.setFields(fields);
    }

    @Override
    public SelectLifts setKey(java.lang.String key) {
      return (SelectLifts) super.setKey(key);
    }

    @Override
    public SelectLifts setOauthToken(java.lang.String oauthToken) {
      return (SelectLifts) super.setOauthToken(oauthToken);
    }

    @Override
    public SelectLifts setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SelectLifts) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SelectLifts setQuotaUser(java.lang.String quotaUser) {
      return (SelectLifts) super.setQuotaUser(quotaUser);
    }

    @Override
    public SelectLifts setUserIp(java.lang.String userIp) {
      return (SelectLifts) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String username;

    /**

     */
    public java.lang.String getUsername() {
      return username;
    }

    public SelectLifts setUsername(java.lang.String username) {
      this.username = username;
      return this;
    }

    @Override
    public SelectLifts set(String parameterName, Object value) {
      return (SelectLifts) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "selectSessions".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link SelectSessions#execute()} method to invoke the remote operation.
   *
   * @param username
   * @return the request
   */
  public SelectSessions selectSessions(java.lang.String username) throws java.io.IOException {
    SelectSessions result = new SelectSessions(username);
    initialize(result);
    return result;
  }

  public class SelectSessions extends MyApiRequest<com.liftlog.backend.myApi.model.SessionAPICollection> {

    private static final String REST_PATH = "selectSessions/{username}";

    /**
     * Create a request for the method "selectSessions".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link SelectSessions#execute()} method to invoke the remote operation.
     * <p> {@link SelectSessions#initialize(com.google.api.client.googleapis.services.AbstractGoogleCl
     * ientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param username
     * @since 1.13
     */
    protected SelectSessions(java.lang.String username) {
      super(MyApi.this, "POST", REST_PATH, null, com.liftlog.backend.myApi.model.SessionAPICollection.class);
      this.username = com.google.api.client.util.Preconditions.checkNotNull(username, "Required parameter username must be specified.");
    }

    @Override
    public SelectSessions setAlt(java.lang.String alt) {
      return (SelectSessions) super.setAlt(alt);
    }

    @Override
    public SelectSessions setFields(java.lang.String fields) {
      return (SelectSessions) super.setFields(fields);
    }

    @Override
    public SelectSessions setKey(java.lang.String key) {
      return (SelectSessions) super.setKey(key);
    }

    @Override
    public SelectSessions setOauthToken(java.lang.String oauthToken) {
      return (SelectSessions) super.setOauthToken(oauthToken);
    }

    @Override
    public SelectSessions setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SelectSessions) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SelectSessions setQuotaUser(java.lang.String quotaUser) {
      return (SelectSessions) super.setQuotaUser(quotaUser);
    }

    @Override
    public SelectSessions setUserIp(java.lang.String userIp) {
      return (SelectSessions) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String username;

    /**

     */
    public java.lang.String getUsername() {
      return username;
    }

    public SelectSessions setUsername(java.lang.String username) {
      this.username = username;
      return this;
    }

    @Override
    public SelectSessions set(String parameterName, Object value) {
      return (SelectSessions) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link MyApi}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link MyApi}. */
    @Override
    public MyApi build() {
      return new MyApi(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MyApiRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMyApiRequestInitializer(
        MyApiRequestInitializer myapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(myapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
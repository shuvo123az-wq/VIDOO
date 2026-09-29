package com.example.data.services

object BackendArchitectureSpec {

    val firestoreCollections = listOf(
        "users" to "Profiles, unique usernames, XP/levels, verification badges, privacy flags",
        "posts" to "Text & photo posts, captions, hashtags, mentions, visibility, ranking signals",
        "videos" to "Long-form 16:9 video metadata, Cloud Storage HLS/DASH URIs, chapters, quality tiers",
        "shortVideos" to "Vertical 9:16 video metadata, loop metrics, audio track reference, duration",
        "comments" to "Top-level post/video comments with cursor pagination and like counters",
        "replies" to "Threaded comment replies indexed by parentCommentId and createdAtTimestamp",
        "likes" to "Composite index (userId_targetId) preventing duplicate likes",
        "followers" to "Inbound creator follower graph edges",
        "following" to "Outbound user following graph edges",
        "friends" to "Mutual verified friend connections",
        "friendRequests" to "Pending/accepted/declined friend invitations",
        "notifications" to "FCM-backed user alerts categorized by social, content, rewards, system",
        "messages" to "Encrypted/private 1-to-1 and group-ready chat messages with media pointers",
        "conversations" to "Chat thread headers, participant IDs, online presence, unread counters",
        "reports" to "Safety & moderation queue items with category, reporterId, and review status",
        "rewards" to "Server-authoritative Vidoo Coins ledger and lifetime points summary",
        "rewardTransactions" to "Immutable audit log of coin/XP grants with SSV cryptographic signatures",
        "dailyChallenges" to "Configurable daily engagement quests and claim states",
        "streaks" to "Daily login streak counters and server timestamp checkpoints",
        "subscriptions" to "Google Play Billing purchase tokens, plan entitlements (FREE/PREMIUM/CREATOR)",
        "creatorAccounts" to "Creator Studio enrollment, payout status, and policy compliance",
        "creatorAnalytics" to "Aggregated watch time, completion rate, eligible views, estimated revenue",
        "adSettings" to "Remote AdService controls (enableAds, placement toggles, frequency caps)",
        "appSettings" to "Global feature flags, maintenance mode, upload limits, XP/reward thresholds",
        "hashtags" to "Trending hashtag counters and discovery indexes",
        "savedPosts" to "User bookmarked posts, Shorts, and Long Videos"
    )

    const val FIRESTORE_SECURITY_RULES = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() { return request.auth != null; }
    function isOwner(userId) { return isSignedIn() && request.auth.uid == userId; }
    function isAdmin() { return isSignedIn() && request.auth.token.admin == true; }

    // Users: Clients cannot modify coinBalance, level, isVerified, or accountStatus directly
    match /users/{userId} {
      allow read: if isSignedIn();
      allow create: if isOwner(userId)
        && !('coinBalance' in request.resource.data)
        && !('isVerified' in request.resource.data);
      allow update: if isOwner(userId)
        && !request.resource.data.diff(resource.data).affectedKeys()
            .hasAny(['coinBalance', 'level', 'currentXp', 'isVerified', 'accountStatus', 'subscriptionTier']);
    }

    // Server-authoritative collections (Cloud Functions Admin SDK writes only)
    match /rewards/{userId} {
      allow read: if isOwner(userId) || isAdmin();
      allow write: if false; // Strictly controlled by Cloud Functions
    }
    match /rewardTransactions/{txId} {
      allow read: if isSignedIn() && resource.data.userId == request.auth.uid;
      allow write: if false; // Strictly controlled by AdMob SSV & Reward Cloud Functions
    }
    match /subscriptions/{subId} {
      allow read: if isOwner(subId) || isAdmin();
      allow write: if false; // Validated via Google Play Developer API webhook
    }
    match /creatorAnalytics/{creatorId} {
      allow read: if isOwner(creatorId) || isAdmin();
      allow write: if false; // Computed server-side
    }
    match /adSettings/{docId} {
      allow read: if true;
      allow write: if isAdmin();
    }
    match /appSettings/{docId} {
      allow read: if true;
      allow write: if isAdmin();
    }
  }
}
"""

    const val STORAGE_SECURITY_RULES = """
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Profile, Cover, Post Images, Thumbnails, Short Videos, Long Videos, Chat Media
    // Large video binaries are stored here and NEVER inside Firestore documents.
    match /users/{userId}/{mediaFolder}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null
                   && request.auth.uid == userId
                   && request.resource.size < 500 * 1024 * 1024 // 500 MB configurable cap
                   && request.resource.contentType.matches('image/.*|video/.*');
    }
  }
}
"""

    const val CLOUD_FUNCTIONS_BLUEPRINT = """
// Firebase Cloud Functions (TypeScript) — Server-Side Validation
export const verifyRewardedAdSsv = onRequest(async (req, res) => {
  // 1. Verify Google AdMob Server-Side Verification (SSV) ECDSA signature
  // 2. Check nonce uniqueness in rewardTransactions to prevent replay abuse
  // 3. Atomically credit VIDOO COINS & XP inside Firestore transaction
});

export const validateCreatorEligibility = onSchedule("every 24 hours", async () => {
  // Reads configurable thresholds from appSettings/creatorEligibility
  // Evaluates followers, views, watchTime, accountAge, and policy compliance
});
"""

    const val COMMUNITY_GUIDELINES = """
VIDOO COMMUNITY GUIDELINES
1. Authentic & Respectful Connection: Treat fellow creators and viewers with respect. Harassment, bullying, hate speech, and threats are strictly prohibited.
2. Safe & Appropriate Media: Do not upload sexually explicit content, graphic violence, dangerous challenges, or self-harm encouragement.
3. Original Creativity & Attribution: Share content you created or have licensed rights to use. Use the Vidoo Royalty-Cleared Music Library for background audio.
4. Platform Integrity: Artificial view inflation, fake engagement, reward manipulation, or referral spam will result in coin forfeiture and account suspension.
"""

    const val COPYRIGHT_POLICY = """
VIDOO COPYRIGHT & DMCA POLICY
• Vidoo respects intellectual property rights and complies with the Digital Millennium Copyright Act (DMCA).
• Users may only select music from Vidoo's licensed royalty-cleared library or upload original audio they own.
• Required music credits and license codes are automatically displayed on Shorts and Long Videos.
• Copyright owners may submit takedown notices via the in-app Report -> Copyright Concern flow or legal@vidoo.app. Repeat infringers are permanently terminated.
"""

    const val PRIVACY_POLICY = """
VIDOO PRIVACY POLICY (GOOGLE PLAY DATA SAFETY COMPLIANT)
• Data Collected: Account profile (name, username, email, optional phone), user-generated content (posts, videos, comments, messages), and app performance/analytics diagnostics.
• Media Picker Privacy: Vidoo uses the Android zero-permission Photo & Video Picker. We never access your entire media library without explicit per-item selection.
• Data Usage: Used to deliver personalized feeds, creator analytics, server-verified Vidoo Coins rewards, and safety moderation.
• Account & Data Deletion: Users can permanently delete their account and all associated personal data at any time via Settings -> Account -> Delete Account or via our web portal.
"""

    const val TERMS_OF_SERVICE = """
VIDOO TERMS OF SERVICE
1. Acceptance: By using VIDOO ("Watch. Share. Connect."), you agree to these Terms and our Community Guidelines.
2. Virtual Points Disclaimer: VIDOO COINS are virtual platform points earned through eligible in-app activities. VIDOO COINS have no cash value, are non-transferable outside the platform, and do not constitute real money or financial instruments.
3. Subscriptions: PREMIUM and CREATOR plans are billed via Google Play Billing and auto-renew unless cancelled in Google Play Subscriptions at least 24 hours before renewal.
4. Content License: You retain ownership of your original videos and posts while granting Vidoo a non-exclusive license to host, transcode, and stream your content according to your selected visibility (Public, Followers, Private).
"""
}

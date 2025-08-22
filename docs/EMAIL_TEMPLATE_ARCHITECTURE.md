# 📧 Email Template Architecture

## 🎯 Overview

LegacyKeep Notification Service uses **Thymeleaf + MJML** for creating **light, stylish, and responsive emails** that work perfectly across all devices and email clients.

## 🏗️ Architecture

### **Template Engine Stack**
```
Thymeleaf (Server-side) → MJML (Email Framework) → HTML Email → Email Client
```

### **Key Components**
- **Thymeleaf** - Server-side template engine
- **MJML** - Responsive email framework
- **Spring Mail** - Email sending service
- **Template Engine** - Template management and rendering

---

## 🛠️ Dependencies

### **Core Dependencies**
```xml
<!-- Thymeleaf Template Engine -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Spring Mail for Email Sending -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- MJML for Responsive Email Templates -->
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>mjml</artifactId>
    <version>4.14.1</version>
</dependency>

<!-- Jackson for JSON Processing -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### **Development Dependencies**
```xml
<!-- MJML Development Tools -->
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>mjml-cli</artifactId>
    <version>4.14.1</version>
    <scope>test</scope>
</dependency>
```

---

## 📁 Template Structure

### **Directory Layout**
```
src/main/resources/
├── templates/
│   ├── email/
│   │   ├── base/
│   │   │   ├── layout.html          # Base email layout
│   │   │   ├── header.html          # Email header component
│   │   │   ├── footer.html          # Email footer component
│   │   │   └── styles.html          # Common styles
│   │   ├── auth/
│   │   │   ├── email-verification.html
│   │   │   ├── password-reset.html
│   │   │   ├── welcome.html
│   │   │   └── account-locked.html
│   │   ├── family/
│   │   │   ├── invitation.html
│   │   │   ├── member-joined.html
│   │   │   └── story-shared.html
│   │   ├── notifications/
│   │   │   ├── story-created.html
│   │   │   ├── family-update.html
│   │   │   └── reminder.html
│   │   └── marketing/
│   │       ├── newsletter.html
│   │       ├── feature-update.html
│   │       └── promotion.html
│   └── fragments/
│       ├── common/
│       │   ├── button.html
│       │   ├── card.html
│       │   ├── divider.html
│       │   └── social-links.html
│       └── sections/
│           ├── hero.html
│           ├── content.html
│           ├── cta.html
│           └── footer.html
```

---

## 🎨 Base Email Layout

### **MJML Base Template**
```html
<!-- templates/email/base/layout.html -->
<mjml>
  <mj-head>
    <mj-title>LegacyKeep - {{title}}</mj-title>
    <mj-font name="Inter" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" />
    <mj-attributes>
      <mj-all font-family="Inter, Arial, sans-serif" />
      <mj-text font-size="16px" line-height="24px" color="#333333" />
      <mj-button background-color="#4F46E5" color="white" font-weight="600" />
    </mj-attributes>
  </mj-head>
  
  <mj-body background-color="#F9FAFB">
    <!-- Header -->
    <mj-section background-color="#FFFFFF" padding="20px">
      <mj-column>
        <mj-image src="{{logoUrl}}" alt="LegacyKeep" width="150px" />
      </mj-column>
    </mj-section>
    
    <!-- Content -->
    <mj-section background-color="#FFFFFF" padding="40px 20px">
      <mj-column>
        <mj-text font-size="24px" font-weight="600" color="#111827" padding-bottom="20px">
          {{title}}
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          {{content}}
        </mj-text>
        
        <!-- Dynamic Content -->
        <th:block th:replace="${content}">
          <!-- Template-specific content goes here -->
        </th:block>
      </mj-column>
    </mj-section>
    
    <!-- Footer -->
    <mj-section background-color="#F3F4F6" padding="20px">
      <mj-column>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          © 2025 LegacyKeep. All rights reserved.
        </mj-text>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          <a href="{{unsubscribeUrl}}" style="color: #6B7280;">Unsubscribe</a> | 
          <a href="{{privacyUrl}}" style="color: #6B7280;">Privacy Policy</a>
        </mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
```

---

## 📧 Email Templates

### **1. 🔐 Email Verification Template**
```html
<!-- templates/email/auth/email-verification.html -->
<mjml>
  <mj-head>
    <mj-title>Verify Your Email - LegacyKeep</mj-title>
    <mj-font name="Inter" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" />
    <mj-attributes>
      <mj-all font-family="Inter, Arial, sans-serif" />
    </mj-attributes>
  </mj-head>
  
  <mj-body background-color="#F9FAFB">
    <!-- Header -->
    <mj-section background-color="#FFFFFF" padding="20px">
      <mj-column>
        <mj-image src="https://legacykeep.com/logo.png" alt="LegacyKeep" width="150px" />
      </mj-column>
    </mj-section>
    
    <!-- Hero Section -->
    <mj-section background-color="#FFFFFF" padding="40px 20px">
      <mj-column>
        <mj-text font-size="28px" font-weight="600" color="#111827" text-align="center" padding-bottom="10px">
          Welcome to LegacyKeep! 🎉
        </mj-text>
        <mj-text font-size="18px" color="#6B7280" text-align="center" padding-bottom="30px">
          Let's verify your email to get started
        </mj-text>
      </mj-column>
    </mj-section>
    
    <!-- Content Section -->
    <mj-section background-color="#FFFFFF" padding="0 20px 40px">
      <mj-column>
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          Hi <strong th:text="${userName}">User</strong>,
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          Thank you for joining LegacyKeep! We're excited to help you preserve your family's precious stories and memories.
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="30px">
          Please click the button below to verify your email address and complete your registration:
        </mj-text>
        
        <!-- CTA Button -->
        <mj-button background-color="#4F46E5" color="white" font-weight="600" 
                   href="https://legacykeep.com/verify?token={{verificationToken}}" 
                   padding="15px 30px" border-radius="8px">
          Verify Email Address
        </mj-button>
        
        <mj-text font-size="14px" color="#6B7280" text-align="center" padding-top="20px">
          This link will expire in 24 hours
        </mj-text>
      </mj-column>
    </mj-section>
    
    <!-- Footer -->
    <mj-section background-color="#F3F4F6" padding="20px">
      <mj-column>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          © 2025 LegacyKeep. All rights reserved.
        </mj-text>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          <a href="https://legacykeep.com/privacy" style="color: #6B7280;">Privacy Policy</a> | 
          <a href="https://legacykeep.com/help" style="color: #6B7280;">Help Center</a>
        </mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
```

### **2. 🔑 Password Reset Template**
```html
<!-- templates/email/auth/password-reset.html -->
<mjml>
  <mj-head>
    <mj-title>Reset Your Password - LegacyKeep</mj-title>
    <mj-font name="Inter" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" />
    <mj-attributes>
      <mj-all font-family="Inter, Arial, sans-serif" />
    </mj-attributes>
  </mj-head>
  
  <mj-body background-color="#F9FAFB">
    <!-- Header -->
    <mj-section background-color="#FFFFFF" padding="20px">
      <mj-column>
        <mj-image src="https://legacykeep.com/logo.png" alt="LegacyKeep" width="150px" />
      </mj-column>
    </mj-section>
    
    <!-- Content Section -->
    <mj-section background-color="#FFFFFF" padding="40px 20px">
      <mj-column>
        <mj-text font-size="24px" font-weight="600" color="#111827" text-align="center" padding-bottom="20px">
          Reset Your Password 🔐
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          Hi <strong th:text="${userName}">User</strong>,
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          We received a request to reset your LegacyKeep password. If you didn't make this request, you can safely ignore this email.
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="30px">
          Click the button below to create a new password:
        </mj-text>
        
        <!-- CTA Button -->
        <mj-button background-color="#DC2626" color="white" font-weight="600" 
                   href="https://legacykeep.com/reset-password?token={{resetToken}}" 
                   padding="15px 30px" border-radius="8px">
          Reset Password
        </mj-button>
        
        <mj-text font-size="14px" color="#6B7280" text-align="center" padding-top="20px">
          This link will expire in 1 hour for security
        </mj-text>
      </mj-column>
    </mj-section>
    
    <!-- Footer -->
    <mj-section background-color="#F3F4F6" padding="20px">
      <mj-column>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          © 2025 LegacyKeep. All rights reserved.
        </mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
```

### **3. 👨‍👩‍👧‍👦 Family Invitation Template**
```html
<!-- templates/email/family/invitation.html -->
<mjml>
  <mj-head>
    <mj-title>You're Invited to Join a Family Circle - LegacyKeep</mj-title>
    <mj-font name="Inter" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" />
    <mj-attributes>
      <mj-all font-family="Inter, Arial, sans-serif" />
    </mj-attributes>
  </mj-head>
  
  <mj-body background-color="#F9FAFB">
    <!-- Header -->
    <mj-section background-color="#FFFFFF" padding="20px">
      <mj-column>
        <mj-image src="https://legacykeep.com/logo.png" alt="LegacyKeep" width="150px" />
      </mj-column>
    </mj-section>
    
    <!-- Hero Section -->
    <mj-section background-color="#FFFFFF" padding="40px 20px">
      <mj-column>
        <mj-text font-size="28px" font-weight="600" color="#111827" text-align="center" padding-bottom="10px">
          You're Invited! 🎉
        </mj-text>
        <mj-text font-size="18px" color="#6B7280" text-align="center" padding-bottom="30px">
          Join your family on LegacyKeep
        </mj-text>
      </mj-column>
    </mj-section>
    
    <!-- Content Section -->
    <mj-section background-color="#FFFFFF" padding="0 20px 40px">
      <mj-column>
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          Hi there!
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="20px">
          <strong th:text="${inviterName}">Family Member</strong> has invited you to join their family circle on LegacyKeep, where you can share and preserve your family's precious stories and memories together.
        </mj-text>
        
        <mj-text font-size="16px" line-height="24px" color="#374151" padding-bottom="30px">
          Click the button below to accept the invitation and start sharing your family's legacy:
        </mj-text>
        
        <!-- CTA Button -->
        <mj-button background-color="#059669" color="white" font-weight="600" 
                   href="https://legacykeep.com/join-family?token={{invitationToken}}" 
                   padding="15px 30px" border-radius="8px">
          Accept Invitation
        </mj-button>
        
        <mj-text font-size="14px" color="#6B7280" text-align="center" padding-top="20px">
          This invitation will expire in 7 days
        </mj-text>
      </mj-column>
    </mj-section>
    
    <!-- Footer -->
    <mj-section background-color="#F3F4F6" padding="20px">
      <mj-column>
        <mj-text font-size="14px" color="#6B7280" text-align="center">
          © 2025 LegacyKeep. All rights reserved.
        </mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
```

---

## 🔧 Template Engine Service

### **Email Template Service**
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailTemplateService {
    
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    
    /**
     * Render email template with data
     */
    public String renderTemplate(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }
    
    /**
     * Send email with template
     */
    public void sendTemplateEmail(String to, String subject, String templateName, 
                                 Map<String, Object> data) {
        try {
            String htmlContent = renderTemplate(templateName, data);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send template email: {}", e.getMessage(), e);
            throw new EmailSendException("Failed to send template email", e);
        }
    }
    
    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new EmailSendException("Failed to send email", e);
        }
    }
}
```

---

## 🎨 Styling Guidelines

### **Color Palette**
```css
/* Primary Colors */
--primary-500: #4F46E5;    /* Main brand color */
--primary-600: #4338CA;    /* Darker shade */
--primary-400: #6366F1;    /* Lighter shade */

/* Success Colors */
--success-500: #059669;    /* Green for positive actions */
--success-600: #047857;

/* Warning Colors */
--warning-500: #D97706;    /* Orange for warnings */
--warning-600: #B45309;

/* Error Colors */
--error-500: #DC2626;      /* Red for errors/security */
--error-600: #B91C1C;

/* Neutral Colors */
--gray-50: #F9FAFB;       /* Background */
--gray-100: #F3F4F6;      /* Light background */
--gray-500: #6B7280;      /* Secondary text */
--gray-700: #374151;      /* Body text */
--gray-900: #111827;      /* Headings */
```

### **Typography**
```css
/* Font Family */
font-family: 'Inter', Arial, sans-serif;

/* Font Sizes */
--text-xs: 12px;          /* Small text */
--text-sm: 14px;          /* Footer, captions */
--text-base: 16px;        /* Body text */
--text-lg: 18px;          /* Subheadings */
--text-xl: 20px;          /* Large text */
--text-2xl: 24px;         /* Section headings */
--text-3xl: 28px;         /* Hero headings */

/* Line Heights */
--leading-tight: 1.25;    /* Headings */
--leading-normal: 1.5;    /* Body text */
--leading-relaxed: 1.75;  /* Large text */
```

### **Spacing**
```css
/* Padding/Margin */
--space-1: 4px;
--space-2: 8px;
--space-3: 12px;
--space-4: 16px;
--space-5: 20px;
--space-6: 24px;
--space-8: 32px;
--space-10: 40px;
--space-12: 48px;
```

---

## 📱 Responsive Design

### **Mobile-First Approach**
- **Base width**: 600px (standard email width)
- **Mobile breakpoint**: 480px
- **Tablet breakpoint**: 768px

### **Responsive Features**
- **Fluid images** - Scale with container
- **Stackable columns** - Single column on mobile
- **Touch-friendly buttons** - Minimum 44px height
- **Readable text** - Minimum 14px font size
- **Adequate spacing** - Touch targets well-spaced

### **Email Client Compatibility**
- **Gmail** - Full support
- **Outlook** - Good support (fallbacks for older versions)
- **Apple Mail** - Full support
- **Yahoo Mail** - Good support
- **Thunderbird** - Good support

---

## 🚀 Performance Optimization

### **File Size Optimization**
- **Compressed images** - WebP format with fallbacks
- **Minified CSS** - Remove unnecessary whitespace
- **Inline styles** - Critical CSS inlined
- **Optimized fonts** - System fonts as fallbacks

### **Loading Speed**
- **CDN images** - Fast global delivery
- **Cached templates** - Template compilation caching
- **Async processing** - Non-blocking email sending
- **Queue management** - Background processing

### **Best Practices**
- **Keep under 100KB** - Total email size
- **Optimize images** - Compress and resize
- **Use web fonts sparingly** - System fonts preferred
- **Test across clients** - Regular compatibility testing

---

## 🧪 Testing Strategy

### **Email Client Testing**
- **Gmail** (Web, iOS, Android)
- **Outlook** (Desktop, Web, Mobile)
- **Apple Mail** (iOS, macOS)
- **Yahoo Mail** (Web, Mobile)
- **Thunderbird** (Desktop)

### **Device Testing**
- **iPhone** (iOS 13+)
- **Android** (8+)
- **Desktop** (Windows, macOS, Linux)
- **Tablet** (iPad, Android tablets)

### **Testing Tools**
- **Email on Acid** - Professional testing
- **Litmus** - Email client testing
- **Mailtrap** - Development testing
- **Browser dev tools** - Responsive testing

---

## 📊 Analytics & Tracking

### **Email Metrics**
- **Open rate** - Track email opens
- **Click rate** - Track button clicks
- **Bounce rate** - Monitor delivery issues
- **Unsubscribe rate** - Track opt-outs

### **Implementation**
```html
<!-- Open tracking pixel -->
<img src="https://legacykeep.com/track/open/{{emailId}}" width="1" height="1" style="display:none;" />

<!-- Click tracking -->
<a href="https://legacykeep.com/track/click/{{emailId}}/{{linkId}}?url={{originalUrl}}" 
   style="text-decoration: none;">
  Button Text
</a>
```

---

**Last Updated**: August 21, 2025  
**Version**: 1.0.0
